package ru.yarsu.web.handlers.announcement

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import org.http4k.lens.WebForm
import ru.yarsu.domain.entities.AuthUser
import ru.yarsu.domain.entities.AuthorizationMethods
import ru.yarsu.domain.operations.announcement.GetAnnouncementOperation
import ru.yarsu.web.lenses.AnnouncementLenses
import ru.yarsu.web.lenses.CategoryLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.NewAnnouncementVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditAnnouncementHandler(
    private val htmlView: ContextAwareViewRender,
    private val authMethods: AuthorizationMethods,
    private val getAuthUser: RequestContextLens<AuthUser?>,
    private val getAnnouncement: GetAnnouncementOperation,
    private val categoryLenses: CategoryLenses,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val announcementId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)
        if (!authMethods.authAddOrEditAnnouncement(
                getAuthUser(request),
                announcementId,
            )
        ) {
            return Response(Status.FORBIDDEN)
        }
        val announcement =
            getAnnouncement.get(announcementId)
                ?: return Response(Status.NOT_FOUND)

        val webForm =
            WebForm().with(
                AnnouncementLenses.announcementIdField of announcement.id,
                categoryLenses.categoryField of announcement.category,
                AnnouncementLenses.titleField of announcement.title,
                AnnouncementLenses.descriptionField of announcement.description,
            )

        return Response(Status.OK).with(
            htmlView(request) of
                NewAnnouncementVM(
                    webForm,
                ),
        )
    }
}
