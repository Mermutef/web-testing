package ru.yarsu.web.handlers.specialist

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.cookies
import java.time.Instant

/**
 * Пользователь перенаправляется на главную страницу с инвалидированным cookie "auth" с jwt-токеном в ответе.
 * Если токен ранее не был установлен, поведение не должно измениться.
 */

class LogoutHandlerTest : StringSpec({
    "Должен перенаправлять на главную страницу" {
        val handler = LogoutHandler()
        val request = Request(Method.GET, "/logout")

        val response = handler(request)

        response.status shouldBe Status.FOUND
        response.header("Location") shouldBe "/"
    }

    "Должен инвалидировать auth cookie при выходе" {
        val handler = LogoutHandler()
        val request =
            Request(Method.GET, "/logout")
                .cookie(Cookie("auth", "some-token", expires = Instant.MAX))

        val response = handler(request)

        val authCookie = response.cookies().firstOrNull { it.name == "auth" }
        authCookie.shouldNotBeNull()
        authCookie.value.shouldBeEmpty()
        authCookie.maxAge shouldBe 0
    }

    "Должен работать без auth cookie" {
        val handler = LogoutHandler()
        val request = Request(Method.GET, "/logout")

        val response = handler(request)

        response.status shouldBe Status.FOUND
        response.header("Location") shouldBe "/"

        val authCookie = response.cookies().firstOrNull { it.name == "auth" }
        authCookie.shouldNotBeNull()
        authCookie.value.shouldBeEmpty()
        authCookie.maxAge shouldBe 0
    }
})
