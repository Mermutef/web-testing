package ru.yarsu.domain.entities

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import java.time.Instant

const val SECONDS_IN_HALF_HOUR = 60 * 30 * 1L
const val SECONDS_IN_DAY = 60 * 60 * 24 * 1L
const val DAYS_IN_WEEK = 7 * 1L

class JwtTools(
    secretKey: String,
    private val company: String,
    val tokenLifetime: Long = DAYS_IN_WEEK,
) {
    private val algorithm = Algorithm.HMAC512(secretKey)
    private val verifier = JWT.require(algorithm).withIssuer(company).acceptExpiresAt(SECONDS_IN_HALF_HOUR).build()

    fun createToken(id: Int): String? {
        return try {
            JWT.create().withSubject(id.toString()).withExpiresAt(
                Instant.now().plusSeconds(tokenLifetime * SECONDS_IN_DAY),
            ).withIssuer(company).sign(algorithm)
        } catch (_: JWTCreationException) {
            null
        }
    }

    fun validateToken(token: String): Int? {
        return try {
            verifier.verify(token).subject.toIntOrNull()
        } catch (_: JWTVerificationException) {
            null
        }
    }
}
