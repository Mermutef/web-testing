package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import ru.yarsu.web.handlers.filter.ForbiddenHandler
import ru.yarsu.web.templates.ContextAwareViewRender

class ForbiddenFilter(
    private val htmlView: ContextAwareViewRender,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        ForbiddenHandler(
            next,
            htmlView,
        )
}
