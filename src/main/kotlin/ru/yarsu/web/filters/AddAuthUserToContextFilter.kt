package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.AuthUser
import ru.yarsu.domain.entities.JwtTools
import ru.yarsu.domain.operations.specialist.CreateAuthUserByLogin
import ru.yarsu.web.handlers.filter.AuthUserHandler

class AddAuthUserToContextFilter(
    private val addAuthUserToContextLens: RequestContextLens<AuthUser?>,
    private val createAuthUser: CreateAuthUserByLogin,
    private val jwtTools: JwtTools,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        AuthUserHandler(
            next,
            addAuthUserToContextLens,
            createAuthUser,
            jwtTools,
        )
}
