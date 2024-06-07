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
import ru.yarsu.domain.entities.EntitiesCheckAndHelpMethods

class SpecialistLenses(degreeLenses: DegreeLenses) {
    val allSpecialistFormLenses =
        Body.webForm(
            Validator.Feedback,
            fcsField,
            degreeLenses.mainDegreeField,
            DegreeLenses.courseDegreeField,
            phoneField,
            vkidField,
            roleField,
            loginField,
            passwordField,
            passwordDuplicateField,
        ).toLens()

    val signInFormLenses =
        Body.webForm(
            Validator.Feedback,
            signInLoginField,
            signInPasswordField,
        ).toLens()

    val editRoleLenses =
        Body.webForm(
            Validator.Feedback,
            roleField,
        ).toLens()

    companion object {
        val fcsField = FormField.nonEmptyString().nonBlankString().required("fcs", "Укажите ФИО")

        val phoneField =
            FormField.nonEmptyString().map(
                BiDiMapping(
                    asOut = { value: String ->
                        EntitiesCheckAndHelpMethods.phoneFormat(value.trim())
                            .takeIf { EntitiesCheckAndHelpMethods.checkPhone(it) }.let {
                                it ?: throw IllegalArgumentException(
                                    "Entered string is not phone or includes not only digits: $value",
                                )
                            }
                    },
                    asIn = { value: String -> value },
                ),
            ).required(
                "phone",
                "Укажите номер телефона для связи. Допустимы только десятичные цифры, пробелы, знаки '-' и префикс '+'",
            )

        val vkidField =
            FormField.nonEmptyString().map(
                BiDiMapping(
                    asOut = { value: String ->
                        value.trim().takeIf { EntitiesCheckAndHelpMethods.checkSocialId(it) }
                            .let { it ?: throw IllegalArgumentException("Entered string is not valid vkid token") }
                    },
                    asIn = { value: String -> value },
                ),
            ).required(
                "vkid",
                "Укажите индентификатор вашего профиля ВКонтакте. Допустимы только латинские буквы, цифры и знак '-'",
            )

        val roleField =
            FormField.int().map(
                BiDiMapping(
                    asOut = { value: Int ->
                        require(value in 1..3) {
                            "Wrong role id"
                        }
                        value
                    },
                    asIn = { value: Int -> value },
                ),
            ).defaulted("role", 1, "Выберите роль из списка, а не из головы :)")

        val loginField =
            FormField.nonEmptyString().map(
                BiDiMapping(
                    asOut = { value: String ->
                        value.trim().takeIf { EntitiesCheckAndHelpMethods.checkLogin(it) }
                            .let { it ?: throw IllegalArgumentException("Entered string is not valid login") }
                    },
                    asIn = { value: String -> value },
                ),
            ).required(
                "login",
                "Логин может иметь длину от 2 до 20 символов",
            )

        val passwordField =
            FormField.nonEmptyString().map(
                BiDiMapping(
                    asOut = { value: String ->
                        value.trim().takeIf { EntitiesCheckAndHelpMethods.checkPassword(it) }
                            .let { it ?: throw IllegalArgumentException("Entered string is not valid password") }
                    },
                    asIn = { value: String -> value },
                ),
            ).required(
                "password",
                "Пароль может иметь длину от 8 символов",
            )

        val passwordDuplicateField =
            FormField.nonEmptyString().map(
                BiDiMapping(
                    asOut = { value: String ->
                        value.trim().takeIf { EntitiesCheckAndHelpMethods.checkPassword(it) }
                            .let { it ?: throw IllegalArgumentException("Entered string is not valid password") }
                    },
                    asIn = { value: String -> value },
                ),
            ).required(
                "passwordDuplicate",
                "",
            )

        val signInLoginField =
            FormField.nonEmptyString().nonBlankString().required(
                "login",
                "Введите логин",
            )

        val signInPasswordField =
            FormField.nonEmptyString().nonBlankString().required(
                "password",
                "Введите пароль",
            )
    }
}
