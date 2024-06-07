package ru.yarsu.web.handlers.specialist

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.getFirst
import org.http4k.core.queries
import org.http4k.core.toParametersMap
import org.http4k.core.with
import ru.yarsu.domain.operations.specialist.DateTimeFilterOperation
import ru.yarsu.web.lenses.AnnouncementLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.SpecialistListVM
import ru.yarsu.web.templates.ContextAwareViewRender

class SpecialistListHandler(
    private val htmlView: ContextAwareViewRender,
    private val dateTimeFilter: DateTimeFilterOperation,
) : HttpHandler {
    // dateIsCorrect определяет наличие/отсутствие атрибута hidden у элемента,
    // сообщающем пользователю о неверных данных
    private var dateIsCorrect = false

    override fun invoke(request: Request): Response {
        val requestParameters =
            request
                .uri
                .queries()
                .toParametersMap()

        val minRegisterDateInput =
            requestParameters
                .getFirst("minFilterDate")
                .orEmpty()

        val maxRegisterDateInput =
            requestParameters
                .getFirst("maxFilterDate")
                .orEmpty()

        val page = UniversalLenses.lensOrNull(UniversalLenses.pageLens, request) ?: 0

        val minRegisterDate = AnnouncementLenses.checkDate(request, "minFilterDate")
        val maxRegisterDate = AnnouncementLenses.checkDate(request, "maxFilterDate")

        val filteredSpecialists =
            dateTimeFilter.dateTimeFilter(
                page,
                minRegisterDate,
                maxRegisterDate,
                request.removeQuery("page").query("page", "$page").uri,
            )

        // detekt ругается, но не вижу смысла сокращать тут условия, потому что это лишь раздует код
        if ((minRegisterDateInput.isEmpty() && maxRegisterDateInput.isEmpty()) ||
            (minRegisterDateInput.isEmpty() && maxRegisterDate != null) ||
            (maxRegisterDateInput.isEmpty() && minRegisterDate != null)
        ) {
            dateIsCorrect = true
        }

        return Response(Status.OK).with(
            htmlView(request) of
                SpecialistListVM(
                    filteredSpecialists,
                    minRegisterDateInput,
                    maxRegisterDateInput,
                    dateIsCorrect,
                ),
        )
    }
}
