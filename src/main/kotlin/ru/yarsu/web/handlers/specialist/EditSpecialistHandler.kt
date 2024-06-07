package ru.yarsu.web.handlers.specialist

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import org.http4k.lens.WebForm
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.degree.EncodeDegreesOperation
import ru.yarsu.domain.operations.degree.GetMainDegreesOperation
import ru.yarsu.web.lenses.DegreeLenses
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.NewSpecialistVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditSpecialistHandler(
    private val htmlView: ContextAwareViewRender,
    private val getAuthUser: RequestContextLens<Specialist?>,
    private val getDegrees: EncodeDegreesOperation,
    private val getMainDegrees: GetMainDegreesOperation,
    private val degreeLenses: DegreeLenses,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val specialistId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)
        val specialist = getAuthUser(request) ?: return Response(Status.NOT_FOUND)
        if (specialist.id != specialistId) return Response(Status.NOT_FOUND)
        val degrees = getDegrees.encodeDegrees(specialist)
        return Response(Status.OK).with(
            htmlView(request) of
                NewSpecialistVM(
                    getMainDegrees.getMainDegrees(),
                    WebForm().with(
                        SpecialistLenses.fcsField of specialist.fcs,
                        SpecialistLenses.phoneField of specialist.phone,
                        SpecialistLenses.vkidField of specialist.vkId,
                        SpecialistLenses.loginField of specialist.login,
                        DegreeLenses.courseDegreeField of degrees.filter { it.type == "course" }.map { it.ru },
                        degreeLenses.mainDegreeField of (degrees.find { it.type == "main" }?.id ?: 0),
                    ),
                    true,
                ),
        )
    }
}
