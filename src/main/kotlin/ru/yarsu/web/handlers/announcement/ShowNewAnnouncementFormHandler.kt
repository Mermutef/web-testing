package ru.yarsu.web.handlers.announcement

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.AuthUser
import ru.yarsu.domain.entities.AuthorizationMethods
import ru.yarsu.web.models.NewAnnouncementVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ShowNewAnnouncementFormHandler(
    private val htmlView: ContextAwareViewRender,
    private val authMethods: AuthorizationMethods,
    private val getAuthUser: RequestContextLens<AuthUser?>,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        if (!authMethods.authAddOrEditAnnouncement(getAuthUser(request), null)) {
            Response(Status.FORBIDDEN)
        } else {
            Response(Status.OK).with(
                htmlView(request) of
                    NewAnnouncementVM(
                        null,
                    ),
            )
        }
}
