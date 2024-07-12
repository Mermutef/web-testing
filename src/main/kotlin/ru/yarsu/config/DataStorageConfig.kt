package ru.yarsu.config

import org.http4k.config.Environment
import org.http4k.config.EnvironmentKey
import org.http4k.lens.nonBlankString
import org.http4k.lens.nonEmptyString

data class DataStorageConfig(
    val dataStorage: String,
) {
    companion object {
        val dsLens = EnvironmentKey.nonEmptyString().nonBlankString().required("ds.directory")
        val defaultEnv = Environment.defaults(dsLens of "json")

        fun makeDBConfig(env: Environment): DataStorageConfig = DataStorageConfig(dsLens(env))
    }
}
