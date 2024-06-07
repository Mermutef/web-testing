package ru.yarsu.web.handlers.specialist

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.WebForm
import ru.yarsu.domain.operations.specialist.GetSpecialistOperation
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.EditRoleVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ShowEditRoleFormHandler(
    private val htmlView: ContextAwareViewRender,
    private val getSpecialist: GetSpecialistOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val specialistId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request) ?: return Response(Status.NOT_FOUND)
        val specialist = getSpecialist.get(specialistId) ?: return Response(Status.NOT_FOUND)
        return Response(Status.OK).with(
            htmlView(request) of
                EditRoleVM(
                    WebForm().with(
                        SpecialistLenses.fcsField of specialist.fcs,
                        SpecialistLenses.roleField of specialist.permissions,
                    ),
                ),
        )
    }
}
