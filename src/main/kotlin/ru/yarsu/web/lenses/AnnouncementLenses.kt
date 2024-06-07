package ru.yarsu.web.lenses

import org.http4k.core.Body
import org.http4k.core.Request
import org.http4k.lens.FormField
import org.http4k.lens.LensFailure
import org.http4k.lens.Query
import org.http4k.lens.Validator
import org.http4k.lens.dateTime
import org.http4k.lens.int
import org.http4k.lens.nonBlankString
import org.http4k.lens.nonEmptyString
import org.http4k.lens.webForm
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AnnouncementLenses(
    categoryLenses: CategoryLenses,
) {
    val allAnnouncementFormLenses =
        Body.webForm(
            Validator.Feedback,
            categoryLenses.categoryField,
            titleField,
            descriptionField,
        ).toLens()

    companion object {
        val titleField =
            FormField
                .nonEmptyString()
                .nonBlankString()
                .required("title", "Укажите заголовок")

        val announcementIdField =
            FormField
                .int()
                .optional("announcementId")

        val dateField =
            FormField
                .dateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .optional("date")

        val descriptionField =
            FormField
                .nonEmptyString()
                .nonBlankString()
                .required("description", "Укажите описание")

        fun checkDate(
            request: Request,
            field: String,
        ): LocalDateTime? {
            val dateTimeQueryLens = Query.dateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME).optional(field)
            return try {
                dateTimeQueryLens(request)
            } catch (_: LensFailure) {
                null
            }
        }
    }
}
