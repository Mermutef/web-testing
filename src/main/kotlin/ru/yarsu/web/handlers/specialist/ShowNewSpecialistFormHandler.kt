package ru.yarsu.web.handlers.specialist

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.domain.operations.degree.GetMainDegreesOperation
import ru.yarsu.web.models.NewSpecialistVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ShowNewSpecialistFormHandler(
    private val htmlView: ContextAwareViewRender,
    private val getMainDegreesOperation: GetMainDegreesOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        Response(Status.OK).with(
            htmlView(request) of
                NewSpecialistVM(
                    getMainDegreesOperation.getMainDegrees(),
                    null,
                ),
        )
}
