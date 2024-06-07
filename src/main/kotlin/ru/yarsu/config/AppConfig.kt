package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import java.io.File

data class AppConfig(
    val webPort: Int,
    val authSalt: String,
    val jwtSalt: String,
    val dataStorage: String,
) {
    companion object {
        private val appEnv =
            Environment.from(File("config/app.properties")) overrides
                Environment.JVM_PROPERTIES overrides
                Environment.ENV overrides
                WebConfig.defaultEnv overrides
                DataStorageConfig.defaultEnv

        fun readConfiguration(): AppConfig =
            AppConfig(
                WebConfig.makeWebConfig(appEnv).webPort,
                SecurityConfig.makeSecurityConfig(appEnv).authSalt,
                SecurityConfig.makeSecurityConfig(appEnv).jwtSalt,
                DataStorageConfig.makeDBConfig(appEnv).dataStorage,
            )
    }
}
