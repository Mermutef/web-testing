package ru.yarsu.web.handlers.category

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.web.models.NewCategoryVM
import ru.yarsu.web.templates.ContextAwareViewRender

class ShowNewCategoryFormHandler(
    private val htmlView: ContextAwareViewRender,
) : HttpHandler {
    override fun invoke(request: Request): Response =
        Response(Status.OK).with(
            htmlView(request) of
                NewCategoryVM(
                    null,
                ),
        )
}
