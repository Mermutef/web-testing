package ru.yarsu.web.handlers.announcement

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.DeleteAnnouncementVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ShowDeleteAnnouncementFormHandler(
    private val htmlView: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val announcementId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)

        return Response(Status.OK).with(
            htmlView(request) of
                DeleteAnnouncementVM(
                    announcementId,
                    null,
                ),
        )
    }
}
