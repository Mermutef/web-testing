package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.with
import org.http4k.lens.RequestContextLens

class SetEditModeFilter(
    private val setEditModeLens: RequestContextLens<Boolean?>,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler = { next(it.with(setEditModeLens of true)) }
}
