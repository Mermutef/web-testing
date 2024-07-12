package ru.yarsu.domain.entities

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.time.Instant

const val SECONDS_IN_HALF_HOUR = 60 * 30 * 1L
const val SECONDS_IN_DAY = 60 * 60 * 24 * 1L
const val DAYS_IN_WEEK = 7 * 1L

class JwtTools(
    secretKey: String,
    private val company: String,
    private val tokenLifetime: Long = DAYS_IN_WEEK,
) {
    private val algorithm = Algorithm.HMAC512(secretKey)
    private val verifier = JWT.require(algorithm).withIssuer(company).acceptExpiresAt(SECONDS_IN_HALF_HOUR).build()

    fun createToken(login: String): String {
        return JWT.create().withSubject(login).withExpiresAt(
            Instant.now().plusSeconds(tokenLifetime * SECONDS_IN_DAY),
        ).withIssuer(company).sign(algorithm)
    }

    fun validateToken(token: String): String? {
        return try {
            verifier.verify(token).subject
        } catch (_: JWTVerificationException) {
            null
        }
    }
}
