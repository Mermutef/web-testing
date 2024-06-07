package ru.yarsu.web.handlers.specialist

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.DeleteSpecialistVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ShowDeleteSpecialistFormHandler(
    private val htmlView: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val specialistId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request) ?: return Response(Status.NOT_FOUND)
        return Response(Status.OK).with(
            htmlView(request) of DeleteSpecialistVM(specialistId, null),
        )
    }
}
