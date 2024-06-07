package ru.yarsu.web.handlers.specialist

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.AuthorizationMethods
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.degree.EncodeDegreesOperation
import ru.yarsu.domain.operations.specialist.GetSpecialistOperation
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.SpecialistVM
import ru.yarsu.web.templates.ContextAwareViewRender

class SpecialistHandler(
    private val htmlView: ContextAwareViewRender,
    private val authMethods: AuthorizationMethods,
    private val getSpecialist: GetSpecialistOperation,
    private val getDegrees: EncodeDegreesOperation,
    private val getAuthUserLens: RequestContextLens<Specialist?>,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val specialistId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)
        val authSpecialist = getAuthUserLens(request) ?: return Response(Status.NOT_FOUND)
        val specialist = getSpecialist.get(specialistId) ?: return Response(Status.NOT_FOUND)
        if (!authMethods.authSeeUserInfo(authSpecialist, specialist.id)) return Response(Status.NOT_FOUND)
        return Response(Status.OK).with(
            htmlView(request) of
                SpecialistVM(
                    specialist,
                    getDegrees.encodeDegrees(specialist),
                    specialistId == authSpecialist.id,
                ),
        )
    }
}
