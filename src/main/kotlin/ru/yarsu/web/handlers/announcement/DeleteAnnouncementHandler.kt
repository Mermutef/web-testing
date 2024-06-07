package ru.yarsu.web.handlers.announcement

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.Validator
import org.http4k.lens.webForm
import ru.yarsu.domain.operations.announcement.DeleteAnnouncementOperation
import ru.yarsu.domain.operations.announcement.GetAnnouncementOperation
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.DeleteAnnouncementVM
import ru.yarsu.web.templates.ContextAwareViewRender

class DeleteAnnouncementHandler(
    private val htmlView: ContextAwareViewRender,
    private val getAnnouncement: GetAnnouncementOperation,
    private val deleteAnnouncement: DeleteAnnouncementOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val announcementId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)

        val announcement =
            getAnnouncement.get(announcementId)
                ?: return Response(Status.NOT_FOUND)

        val form =
            Body.webForm(
                Validator.Feedback,
                UniversalLenses.checkBoxField,
                UniversalLenses.checkEnteredIdLens(announcementId),
            ).toLens()(request)

        if (form.errors.isNotEmpty()) {
            return Response(Status.OK).with(
                htmlView(request) of
                    DeleteAnnouncementVM(
                        announcementId,
                        form,
                    ),
            )
        }

        deleteAnnouncement.delete(announcement.id)

        return Response(Status.FOUND).header("Location", "/")
    }
}
