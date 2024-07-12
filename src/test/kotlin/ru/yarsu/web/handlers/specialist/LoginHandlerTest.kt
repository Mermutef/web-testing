package ru.yarsu.web.handlers.specialist

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.body.form
import org.http4k.core.cookie.cookies
import org.http4k.filter.ServerFilters
import org.http4k.lens.BiDiBodyLens
import org.http4k.template.ViewModel
import org.mockito.kotlin.*
import ru.yarsu.domain.entities.*
import ru.yarsu.domain.operations.specialist.AuthorizationSpecialistOperation
import ru.yarsu.web.contexts.ContextTools
import ru.yarsu.web.models.LoginVM
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.web.templates.cacheRenderer
import java.time.LocalDateTime

/**
 * Поля формы:
 * * login - логин пользователя, выполняющего вход
 * * password - пароль пользователя, выполняющего вход
 * При входе обязательно должно проверяться:
 * 1. Пользователь с данным логином существует, иначе возвращается форма со статусом OK
 * 2. Пароль совпадает с паролем в базе данных, иначе возвращается форма со статусом OK
 * При успешном входе пользователь перенаправляется на главную страницу (FOUND "Location"="/")
 * с установленным cookie "auth" с jwt-токеном в ответе.
 */

class LoginHandlerTest : StringSpec({
    val tools = ContextTools(ContextAwareViewRender.withContentType(cacheRenderer(), TEXT_HTML))
    val renderMock: ContextAwareViewRender = mock()
    val lensMock: BiDiBodyLens<ViewModel> = mock()

    whenever(renderMock.invoke(any())).thenReturn(lensMock)
    whenever(lensMock.of<Response>(any())).thenReturn { it }

    val validDegree = Degree(1, "main", "Высшее")
    val validSpecialist =
        Specialist(
            1,
            "Иванов Иван Иванович",
            listOf(validDegree.id),
            "88005553535",
            "test",
            "test",
            "pass",
            LocalDateTime.now(),
            Permissions.SPECIALIST.id,
        )
    val validToken = "valid-token"

    lateinit var users: MutableMap<String, Specialist>

    lateinit var mockAuthorize: AuthorizationSpecialistOperation

    lateinit var jwtTools: JwtTools

    lateinit var handler: HttpHandler

    beforeEach {
        users = mutableMapOf(validSpecialist.login to validSpecialist)

        mockAuthorize =
            spy(
                object : AuthorizationSpecialistOperation {
                    override fun auth(
                        login: String,
                        password: String,
                    ): Specialist? = users[login]?.takeIf { it.password == password }
                },
            )

        jwtTools = mock()

        whenever(jwtTools.createToken(validSpecialist.login)).thenReturn(validToken)

        handler =
            ServerFilters
                .InitialiseRequestContext(tools.appContexts)
                .then(
                    LoginHandler(
                        htmlView = renderMock,
                        authorizationSpecialist = mockAuthorize,
                        jwtTools = jwtTools,
                    ),
                )
    }

    "Должен перенаправлять на главную страницу при успешной аутентификации" {
        val request =
            Request(Method.POST, "/login")
                .form("login", validSpecialist.login)
                .form("password", validSpecialist.password)
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.status shouldBe Status.FOUND
        response.header("Location") shouldBe "/"
    }

    "Должен создавать токен при успешной аутентификации" {
        val request =
            Request(Method.POST, "/login")
                .form("login", validSpecialist.login)
                .form("password", validSpecialist.password)
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.cookies().firstOrNull { it.name == "auth" }?.value shouldBe validToken
    }

    "Должен возвращать ошибку при неверных учетных данных" {
        val request =
            Request(Method.POST, "/login")
                .form("login", validSpecialist.login)
                .form("password", "wrong${validSpecialist.password}")
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.status shouldBe Status.OK
        verify(lensMock).of<Response>(
            check<LoginVM> { model ->
                model.invalidLoginOrPassword shouldNotBe null
            },
        )
    }

    "Должен возвращать ошибку при пустых учетных данных" {
        val request =
            Request(Method.POST, "/login")
                .form("login", "")
                .form("password", "")
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.status shouldBe Status.OK
        verify(lensMock).of<Response>(
            check<LoginVM> { model ->
                model.form shouldNotBe null
                model.form!!.errors.shouldNotBeEmpty()
            },
        )
    }

    "Не должен создавать токен при неверных учетных данных" {
        val request =
            Request(Method.POST, "/login")
                .form("login", validSpecialist.login)
                .form("password", "wrong${validSpecialist.password}")
                .header("content-type", "application/x-www-form-urlencoded")

        handler(request)

        verify(jwtTools, never()).createToken(any())
    }
})
