package ru.yarsu.web.lenses

import org.http4k.core.Body
import org.http4k.lens.BiDiMapping
import org.http4k.lens.FormField
import org.http4k.lens.Validator
import org.http4k.lens.int
import org.http4k.lens.map
import org.http4k.lens.nonBlankString
import org.http4k.lens.nonEmptyString
import org.http4k.lens.webForm
import ru.yarsu.domain.storages.StoragesOperationsAndMethods

class CategoryLenses(private val storagesOperations: StoragesOperationsAndMethods) {
    val categoryField =
        FormField.int().map(
            BiDiMapping(
                asOut = { value: Int ->
                    value.takeIf { storagesOperations.getCategory.get(it) != null }
                        .let { it ?: throw IllegalArgumentException("Can not find category: $value") }
                },
                asIn = { value: Int ->
                    storagesOperations.getCategory.get(value)?.id
                        ?: storagesOperations.getLexiSortedCategories.getLexiSortedCategories().first().id
                },
            ),
        ).required("category", "Выберите корректную категорию из списка")

    companion object {
        val categoryNameField =
            FormField
                .nonEmptyString()
                .nonBlankString()
                .required("categoryName", "Введите название категории")

        val needLicenceField =
            FormField.map(
                BiDiMapping(
                    asOut = { value: String ->
                        value.trim().takeIf { it == "on" }?.let { true }
                            ?: throw IllegalArgumentException("Can not encode licence value: $value")
                    },
                    asIn = { value: Boolean -> if (value) "checked" else "" },
                ),
            ).optional("needLicense")

        val categoryFormLenses =
            Body.webForm(
                Validator.Feedback,
                categoryNameField,
                needLicenceField,
            ).toLens()
    }
}
