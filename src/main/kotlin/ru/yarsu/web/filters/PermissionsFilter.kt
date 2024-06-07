package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.Permissions

class PermissionsFilter(
    val permissionLens: RequestContextLens<Permissions>,
    val canUse: (Permissions) -> Boolean,
) : Filter {
    infix fun filtered(next: HttpHandler): HttpHandler {
        return invoke { next(it) }
    }

    override fun invoke(next: HttpHandler): HttpHandler =
        {
            if (canUse(permissionLens(it))) {
                next(it)
            } else {
                Response(
                    Status.NOT_FOUND,
                )
            }
        }
}
