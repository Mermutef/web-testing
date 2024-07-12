package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.AuthUser
import ru.yarsu.domain.operations.specialist.GetSpecialistOperation
import ru.yarsu.web.lenses.UniversalLenses

class AddCanEditUserPermissionToContextFilter(
    private val getSpecialist: GetSpecialistOperation,
    private val getAuthUserLens: RequestContextLens<AuthUser?>,
    private val addAuthorPermissionToContextLens: RequestContextLens<Boolean?>,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        { request: Request ->
            next(
                request.with(
                    addAuthorPermissionToContextLens of (
                        getAuthUserLens(request)?.id ==
                            (
                                UniversalLenses.lensOrNull(
                                    UniversalLenses.idLens,
                                    request,
                                )?.let { getSpecialist.get(it) }?.id ?: -1
                            )
                    ),
                ),
            )
        }
}
