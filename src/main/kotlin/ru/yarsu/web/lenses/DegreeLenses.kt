package ru.yarsu.web.lenses

import org.http4k.lens.BiDiMapping
import org.http4k.lens.FormField
import org.http4k.lens.int
import org.http4k.lens.map
import org.http4k.lens.nonEmptyString
import ru.yarsu.domain.storages.StoragesOperationsAndMethods

class DegreeLenses(private val storagesOperations: StoragesOperationsAndMethods) {
    val mainDegreeField =
        FormField.int().map(
            BiDiMapping(
                asOut = { value: Int ->
                    value.takeIf { storagesOperations.getDegree.get(it) != null }
                        .let { it ?: throw IllegalArgumentException("Can not find degree: $value") }
                },
                asIn = { value: Int ->
                    storagesOperations.getDegree.get(value)?.id
                        ?: storagesOperations.getMainDegrees.getMainDegrees().first().id
                },
            ),
        ).required("mainDegree", "Выберите основное образование из списка")

    companion object {
        val courseDegreeField = FormField.nonEmptyString().multi.optional("courseDegree")
    }
}
