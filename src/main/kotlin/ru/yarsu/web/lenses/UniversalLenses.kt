package ru.yarsu.web.lenses

import org.http4k.lens.BiDiLens
import org.http4k.lens.BiDiMapping
import org.http4k.lens.FormField
import org.http4k.lens.Lens
import org.http4k.lens.LensFailure
import org.http4k.lens.Path
import org.http4k.lens.Query
import org.http4k.lens.WebForm
import org.http4k.lens.int
import org.http4k.lens.map

object UniversalLenses {
    val checkBoxField =
        FormField.map(
            BiDiMapping(
                asOut = { value: String ->
                    value.trim().takeIf { it == "on" }?.let { true }
                        ?: throw IllegalArgumentException("Can not encode entered checkbox state string: $value")
                },
                asIn = { value: Boolean -> if (value) "checked" else "" },
            ),
        ).required("deleteAgree", "Подтвердите согласие на удаление")

    val idLens = Path.int().of("id")

    val pageLens = Query.int().optional("page")

    fun checkEnteredIdLens(id: Int): BiDiLens<WebForm, Int> {
        return FormField.int().map(
            BiDiMapping(
                asOut = { value: Int ->
                    value.takeIf { it == id }
                        .let { it ?: throw IllegalArgumentException("Found: $value, Expected: $id") }
                },
                asIn = { value: Int -> value },
            ),
        ).required("idCheck", "Ожидаемый номер не совпадает с введенным значением")
    }

    fun <IN : Any, OUT> lensOrNull(
        lens: Lens<IN, OUT?>,
        value: IN,
    ): OUT? =
        try {
            lens.invoke(value)
        } catch (_: LensFailure) {
            null
        }
}
