package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.Permissions
import ru.yarsu.domain.operations.specialist.GetSpecialistOperation
import ru.yarsu.web.lenses.UniversalLenses

class AddRoleNameToContextFilter(
    private val getSpecialist: GetSpecialistOperation,
    private val addRoleNameToContextLens: RequestContextLens<String?>,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        { request: Request ->
            next(
                request.with(
                    addRoleNameToContextLens of
                        UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                            ?.let { getSpecialist.get(it) }
                            .let { Permissions.roleNameById(it?.permissions ?: 0) },
                ),
            )
        }
}
