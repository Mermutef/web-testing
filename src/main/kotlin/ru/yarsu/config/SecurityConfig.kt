package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.nonEmptyString

data class SecurityConfig(
    val authSalt: String,
    val jwtSalt: String,
) {
    companion object {
        val authSaltLens =
            EnvironmentKey.nonEmptyString()
                .required("auth.salt", "Для работы приложения необходима соль для хранения паролей")

        val jwtSaltLens =
            EnvironmentKey.nonEmptyString()
                .required("jwt.salt", "Для работы приложения необходима соль для шифрования данных")

        fun makeSecurityConfig(env: Environment): SecurityConfig = SecurityConfig(authSaltLens(env), jwtSaltLens(env))
    }
}
