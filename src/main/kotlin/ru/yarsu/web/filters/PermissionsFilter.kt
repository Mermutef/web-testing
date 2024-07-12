package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.Permissions

class PermissionsFilter(
    private val permissionLens: RequestContextLens<Permissions>,
    private val canUse: (Permissions) -> Boolean,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        {
            if (canUse(permissionLens(it))) {
                next(it)
            } else {
                Response(
                    Status.FORBIDDEN,
                )
            }
        }
}
