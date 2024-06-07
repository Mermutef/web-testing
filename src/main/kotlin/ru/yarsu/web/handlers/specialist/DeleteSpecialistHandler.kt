package ru.yarsu.web.handlers.specialist

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.Validator
import org.http4k.lens.webForm
import ru.yarsu.domain.operations.specialist.ClearSpecialistAnnouncementsOperation
import ru.yarsu.domain.operations.specialist.DeleteSpecialistOperation
import ru.yarsu.domain.operations.specialist.GetSpecialistOperation
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.DeleteAnnouncementVM
import ru.yarsu.web.templates.ContextAwareViewRender

class DeleteSpecialistHandler(
    private val htmlView: ContextAwareViewRender,
    private val getSpecialist: GetSpecialistOperation,
    private val clearSpecialist: ClearSpecialistAnnouncementsOperation,
    private val deleteSpecialist: DeleteSpecialistOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val specialistId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)

        val specialist =
            getSpecialist.get(specialistId)
                ?: return Response(Status.NOT_FOUND)

        val form =
            Body.webForm(
                Validator.Feedback,
                UniversalLenses.checkBoxField,
                UniversalLenses.checkEnteredIdLens(specialist.id),
            ).toLens()(request)

        if (form.errors.isNotEmpty()) {
            return Response(Status.OK).with(
                htmlView(request) of
                    DeleteAnnouncementVM(
                        specialist.id,
                        form,
                    ),
            )
        }
        clearSpecialist.clear(specialist.id)
        deleteSpecialist.delete(specialist.id)

        return Response(Status.FOUND).header("Location", "/users")
    }
}
