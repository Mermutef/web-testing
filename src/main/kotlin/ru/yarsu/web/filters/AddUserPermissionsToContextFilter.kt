package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.AuthUser
import ru.yarsu.domain.entities.Permissions

class AddUserPermissionsToContextFilter(
    private val getAuthUserLens: RequestContextLens<AuthUser?>,
    private val addPermissionsToContextLens: RequestContextLens<Permissions>,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        {
            next(
                it.with(
                    addPermissionsToContextLens of (getAuthUserLens(it)?.permissions ?: Permissions.GUEST),
                ),
            )
        }
}
