package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.Permissions
import ru.yarsu.domain.entities.Specialist

class SetPermissionsFilter(
    private val getAuthUserLens: RequestContextLens<Specialist?>,
    private val addPermissionsToContextLens: RequestContextLens<Permissions>,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        {
            next(
                it.with(
                    addPermissionsToContextLens of (
                        Permissions.rolePermissionsById(
                            getAuthUserLens(it)?.permissions,
                        )
                    ),
                ),
            )
        }
}
