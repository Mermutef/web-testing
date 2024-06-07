package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import ru.yarsu.web.handlers.filter.NotFoundHandler
import ru.yarsu.web.templates.ContextAwareViewRender

class NotFoundFilter(
    private val htmlView: ContextAwareViewRender,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        NotFoundHandler(
            next,
            htmlView,
        )
}
