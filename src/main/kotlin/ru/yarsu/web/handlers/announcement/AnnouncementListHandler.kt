package ru.yarsu.web.handlers.announcement

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.getFirst
import org.http4k.core.queries
import org.http4k.core.toParametersMap
import org.http4k.core.with
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.announcement.DateTimeFilterOperation
import ru.yarsu.domain.operations.category.GetCategoryOperation
import ru.yarsu.web.lenses.AnnouncementLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.AnnouncementListVM
import ru.yarsu.web.templates.ContextAwareViewRender

class AnnouncementListHandler(
    private val htmlView: ContextAwareViewRender,
    private val getCategory: GetCategoryOperation,
    private val dateTimeFilter: DateTimeFilterOperation,
    private val specialistsByCategory: (Int) -> Map<Int, Specialist?>,
) : HttpHandler {
    // dateIsCorrect определяет наличие/отсутствие атрибута hidden у элемента,
    // сообщающем пользователю о неверных данных
    private var dateIsCorrect = false

    override fun invoke(request: Request): Response {
        val categoryId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)

        val pageCategory =
            getCategory.get(categoryId)
                ?: return Response(Status.NOT_FOUND)

        val requestParameters =
            request
                .uri
                .queries()
                .toParametersMap()

        val minAnnouncementDateInput =
            requestParameters
                .getFirst("minAnnouncementDate")
                .orEmpty()

        val maxAnnouncementDateInput =
            requestParameters
                .getFirst("maxAnnouncementDate")
                .orEmpty()

        val page = UniversalLenses.lensOrNull(UniversalLenses.pageLens, request) ?: 0

        val minAnnouncementDate = AnnouncementLenses.checkDate(request, "minAnnouncementDate")
        val maxAnnouncementDate = AnnouncementLenses.checkDate(request, "maxAnnouncementDate")

        val filteredAnnouncements =
            dateTimeFilter.dateTimeFilter(
                page,
                minAnnouncementDate,
                maxAnnouncementDate,
                categoryId,
                request.removeQuery("page").query("page", "$page").uri,
            )

        // detekt ругается, но не вижу смысла сокращать тут условия, потому что это лишь раздует код
        if ((minAnnouncementDateInput.isEmpty() && maxAnnouncementDateInput.isEmpty()) ||
            (minAnnouncementDateInput.isEmpty() && maxAnnouncementDate != null) ||
            (maxAnnouncementDateInput.isEmpty() && minAnnouncementDate != null)
        ) {
            dateIsCorrect = true
        }

        return Response(Status.OK).with(
            htmlView(request) of
                AnnouncementListVM(
                    filteredAnnouncements,
                    pageCategory,
                    minAnnouncementDateInput,
                    maxAnnouncementDateInput,
                    dateIsCorrect,
                    specialistsByCategory(categoryId),
                ),
        )
    }
}
