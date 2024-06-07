package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.JwtTools
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.specialist.GetSpecialistOperation
import ru.yarsu.web.handlers.filter.AuthUserHandler

class AuthUserFilter(
    private val addAuthUserToContextLens: RequestContextLens<Specialist?>,
    private val getSpecialist: GetSpecialistOperation,
    private val jwtTools: JwtTools,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        AuthUserHandler(
            next,
            addAuthUserToContextLens,
            getSpecialist,
            jwtTools,
        )
}
