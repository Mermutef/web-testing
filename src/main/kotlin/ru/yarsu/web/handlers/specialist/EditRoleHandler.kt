package ru.yarsu.web.handlers.specialist

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.domain.operations.specialist.EditSpecialistOperation
import ru.yarsu.domain.operations.specialist.GetSpecialistOperation
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.EditRoleVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditRoleHandler(
    private val htmlView: ContextAwareViewRender,
    private val getSpecialist: GetSpecialistOperation,
    private val editSpecialist: EditSpecialistOperation,
    private val specialistLenses: SpecialistLenses,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val form = specialistLenses.editRoleLenses(request)
        if (form.errors.isNotEmpty()) {
            return Response(Status.OK)
                .with(htmlView(request) of EditRoleVM(form))
        }
        val specialistId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request) ?: return Response(Status.NOT_FOUND)
        val specialist = getSpecialist.get(specialistId) ?: return Response(Status.NOT_FOUND)
        editSpecialist.edit(specialist.copy(permissions = SpecialistLenses.roleField(form)))
        return Response(Status.FOUND).header("Location", "/users/${specialist.id}")
    }
}
