package ru.yarsu.web.handlers.filter

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.web.models.ForbiddenVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ForbiddenHandler(
    private val next: HttpHandler,
    private val htmlView: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val response = next(request)
        if (response.status == Status.FORBIDDEN) {
            return response.with(
                htmlView(request) of ForbiddenVM(request.uri.toString()),
            )
        }
        return response
    }
}
