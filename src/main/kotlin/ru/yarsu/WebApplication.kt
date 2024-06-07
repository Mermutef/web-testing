package ru.yarsu

import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.entities.AuthorizationMethods
import ru.yarsu.domain.entities.JwtTools
import ru.yarsu.domain.storages.StoragesOperationsAndMethods
import ru.yarsu.web.contexts.ContextTools
import ru.yarsu.web.filters.AuthUserFilter
import ru.yarsu.web.filters.CategoriesFilter
import ru.yarsu.web.filters.LoggingFilter
import ru.yarsu.web.filters.NotFoundFilter
import ru.yarsu.web.filters.SetPermissionsFilter
import ru.yarsu.web.handlers.HandlersContainer
import ru.yarsu.web.lenses.AnnouncementLenses
import ru.yarsu.web.lenses.CategoryLenses
import ru.yarsu.web.lenses.DegreeLenses
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.router
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.web.templates.cacheRenderer

const val MAIN_CLASS = "ru.yarsu.WebApplication"

fun main() {
    val appConfig = AppConfig.readConfiguration()
    val jwtTools = JwtTools(appConfig.jwtSalt, "SpecialistFinder")
    val path = appConfig.dataStorage
    val loggingFilter = LoggingFilter(MAIN_CLASS)
    val storagesOperations: StoragesOperationsAndMethods
    try {
        storagesOperations = StoragesOperationsAndMethods(appConfig.dataStorage, appConfig.authSalt)
    } catch (ex: IllegalArgumentException) {
        loggingFilter.logInitError(ex, appConfig.dataStorage, "CreateStorages")
        return
    }

    println("Данные взяты из директории /$path")

    val categoryLenses = CategoryLenses(storagesOperations)
    val degreeLenses = DegreeLenses(storagesOperations)
    val announcementLenses = AnnouncementLenses(categoryLenses)
    val specialistLenses = SpecialistLenses(degreeLenses)
    val contextTools = ContextTools(ContextAwareViewRender.withContentType(cacheRenderer(), TEXT_HTML))
    val appWithStaticResources =
        loggingFilter.then(ServerFilters.InitialiseRequestContext(contextTools.appContexts))
            .then(NotFoundFilter(contextTools.htmlViewWithContexts))
            .then(CategoriesFilter(contextTools.allCategoriesLens, storagesOperations)).then(
                AuthUserFilter(
                    contextTools.userAuthLens,
                    storagesOperations.getSpecialist,
                    jwtTools,
                ),
            ).then(
                SetPermissionsFilter(
                    contextTools.userAuthLens,
                    contextTools.userPermissionsLens,
                ),
            ).then(
                routes(
                    router(
                        HandlersContainer(
                            contextTools.htmlViewWithContexts,
                            storagesOperations,
                            announcementLenses,
                            specialistLenses,
                            categoryLenses,
                            degreeLenses,
                            jwtTools,
                            contextTools,
                            AuthorizationMethods(storagesOperations),
                        ),
                    ),
                    static(ResourceLoader.Classpath("/ru/yarsu/public")),
                ),
            )
    storagesOperations.regSDHook()
    val server = appWithStaticResources.asServer(Netty(appConfig.webPort)).start()
    println("Сервер запущен на http://localhost:" + server.port())
    println("Нажмите enter чтобы завершить работу.")
    readln()
    println("Завершение...")
    println("Сохранение данных, не выключайте компьютер и не выдергивайте вилку питания из розетки.")
    server.stop()
    return
}
