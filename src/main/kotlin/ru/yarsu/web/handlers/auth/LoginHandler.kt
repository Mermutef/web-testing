package ru.yarsu.web.handlers.auth

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.with
import ru.yarsu.domain.entities.JwtTools
import ru.yarsu.domain.entities.SECONDS_IN_DAY
import ru.yarsu.domain.operations.specialist.AuthorizationSpecialistOperation
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.models.LoginVM
import ru.yarsu.web.templates.ContextAwareViewRender
import java.time.Instant

class LoginHandler(
    private val htmlView: ContextAwareViewRender,
    private val authorizationSpecialist: AuthorizationSpecialistOperation,
    private val specialistLenses: SpecialistLenses,
    private val jwtTools: JwtTools,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val form = specialistLenses.signInFormLenses(request)
        if (form.errors.isNotEmpty()) {
            return Response(Status.OK).with(
                htmlView(request) of LoginVM(form),
            )
        }
        val login = SpecialistLenses.signInLoginField(form)
        val password = SpecialistLenses.signInPasswordField(form)
        val specialist =
            authorizationSpecialist.auth(login, password) ?: return Response(Status.OK).with(
                htmlView(request) of LoginVM(form.plus("invalidLoginOrPassword" to "Неверный логин или пароль")),
            )
        val jwt = jwtTools.createToken(specialist.id) ?: return Response(Status.NOT_FOUND)
        return Response(Status.FOUND).cookie(
            Cookie("auth", jwt).expires(
                Instant.now()
                    .plusSeconds(jwtTools.tokenLifetime * SECONDS_IN_DAY),
            ).httpOnly(),
        ).header(
            "Location",
            "/",
        )
    }
}
