package ru.yarsu.web.handlers.announcement

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.Announcement
import ru.yarsu.domain.entities.AuthorizationMethods
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.announcement.UpdateAnnouncementOperation
import ru.yarsu.web.lenses.AnnouncementLenses
import ru.yarsu.web.lenses.CategoryLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.NewAnnouncementVM
import ru.yarsu.web.templates.ContextAwareViewRender
import java.time.LocalDateTime

class SaveAnnouncementHandler(
    private val htmlView: ContextAwareViewRender,
    private val authMethods: AuthorizationMethods,
    private val updateAnnouncement: UpdateAnnouncementOperation,
    private val announcementLenses: AnnouncementLenses,
    private val categoryLenses: CategoryLenses,
    private val getAuthUser: RequestContextLens<Specialist?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val specialist = getAuthUser(request)
        val form = announcementLenses.allAnnouncementFormLenses(request)
        val announcementId = UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
        if (specialist == null ||
            !authMethods.authAddOrEditAnnouncement(specialist, announcementId)
        ) {
            return Response(Status.NOT_FOUND)
        }
        if (form.errors.isNotEmpty()) {
            return Response(Status.OK).with(
                htmlView(request) of
                    NewAnnouncementVM(
                        form,
                        true,
                    ),
            )
        }
        val date = LocalDateTime.now()
        val categoryId = categoryLenses.categoryField(form)
        val title = AnnouncementLenses.titleField(form)
        val description = AnnouncementLenses.descriptionField(form)

        return Response(Status.FOUND).header(
            "Location",
            "/announcements/${
                updateAnnouncement.update(
                    Announcement(
                        announcementId ?: -1,
                        date,
                        categoryId,
                        title,
                        description,
                        specialist.id,
                    ),
                )
            }",
        )
    }
}
