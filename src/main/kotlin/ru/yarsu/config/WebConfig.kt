package ru.yarsu.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.lens.int

data class WebConfig(
    val webPort: Int,
) {
    companion object {
        val portLens = EnvironmentKey.int().required("web.port")
        val defaultEnv = Environment.defaults(portLens of 9000)

        fun makeWebConfig(env: Environment): WebConfig = WebConfig(portLens(env))
    }
}
