package ru.yarsu.web.handlers.announcement

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.Uri
import org.http4k.core.findSingle
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.announcement.GetAnnouncementOperation
import ru.yarsu.domain.operations.category.GetCategoryOperation
import ru.yarsu.domain.operations.degree.EncodeDegreesOperation
import ru.yarsu.domain.operations.specialist.GetSpecialistOperation
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.AnnouncementVM
import ru.yarsu.web.templates.ContextAwareViewRender

class AnnouncementHandler(
    private val htmlView: ContextAwareViewRender,
    private val getAnnouncement: GetAnnouncementOperation,
    private val getSpecialist: GetSpecialistOperation,
    private val getCategory: GetCategoryOperation,
    private val getDegrees: EncodeDegreesOperation,
    private val getAutUserLens: RequestContextLens<Specialist?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val announcementId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)
        val announcement =
            getAnnouncement.get(announcementId)
                ?: return Response(Status.NOT_FOUND)
        val specialist =
            getAutUserLens(request)
                ?: getSpecialist.get(announcement.specialist)
                ?: return Response(Status.NOT_FOUND)
        val category =
            getCategory.get(announcement.category)
                ?: return Response(Status.NOT_FOUND)
        val degrees = getDegrees.encodeDegrees(specialist)
        val uriBack =
            Uri
                .of(request.headers.findSingle("Referer") ?: "")
                .query
        return Response(Status.OK).with(
            htmlView(request) of
                AnnouncementVM(
                    announcement,
                    specialist,
                    category,
                    degrees,
                    uriBack,
                    announcement.specialist == getAutUserLens(request)?.id,
                ),
        )
    }
}
