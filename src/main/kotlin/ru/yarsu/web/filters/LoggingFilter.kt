package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path

class LoggingFilter(name: String) : Filter {
    private val logger: Logger

    init {
        if (Files.notExists(Path.of("logs"))) {
            Files.createDirectory(Path.of("logs"))
        }
        logger = LoggerFactory.getLogger(name)
    }

    override fun invoke(next: HttpHandler): HttpHandler =
        { request: Request ->
            val requestTime = System.currentTimeMillis()
            val response = next(request)
            val responseTime = System.currentTimeMillis()
            logger.atInfo().addKeyValue(
                "REQUEST",
                mapOf(
                    "METHOD" to request.method,
                    "URI" to request.uri,
                    "SOURCE" to request.source,
                ),
            ).addKeyValue(
                "RESPONSE",
                mapOf(
                    "STATUS" to response.status,
                    "ELAPSED_TIME" to responseTime - requestTime,
                ),
            ).log()
            response
        }

    fun logInitError(
        error: Exception,
        path: String,
        place: String,
    ) {
        logger.atError().setMessage(error.message).addKeyValue("DIRECTORY", path).addKeyValue("PLACE", place).log()
        System.err.println("Исключение при считывании данных из директории /$path: ${error.message}")
        System.err.println("Аварийное завершение...")
    }
}
