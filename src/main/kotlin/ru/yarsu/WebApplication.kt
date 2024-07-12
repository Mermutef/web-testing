package ru.yarsu

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.routing.ResourceLoader
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import org.slf4j.Logger
import ru.yarsu.config.AppConfig
import ru.yarsu.domain.entities.Announcement
import ru.yarsu.domain.entities.AuthorizationMethods
import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.entities.JwtTools
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.specialist.CreateAuthUserByLoginImpl
import ru.yarsu.domain.storages.AnnouncementStorage
import ru.yarsu.domain.storages.CategoryStorage
import ru.yarsu.domain.storages.DegreeStorage
import ru.yarsu.domain.storages.SpecialistStorage
import ru.yarsu.domain.storages.StoragesOperationsAndMethods
import ru.yarsu.web.contexts.ContextTools
import ru.yarsu.web.filters.AddAuthUserToContextFilter
import ru.yarsu.web.filters.AddCategoriesToContextFilter
import ru.yarsu.web.filters.AddUserPermissionsToContextFilter
import ru.yarsu.web.filters.ForbiddenFilter
import ru.yarsu.web.filters.LoggingFilter
import ru.yarsu.web.filters.NotFoundFilter
import ru.yarsu.web.lenses.AnnouncementLenses
import ru.yarsu.web.lenses.CategoryLenses
import ru.yarsu.web.lenses.DegreeLenses
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.router
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.web.templates.cacheRenderer
import java.io.File
import java.io.FileNotFoundException
import kotlin.concurrent.thread

const val MAIN_CLASS = "ru.yarsu.WebApplication"

fun logInitError(
    error: Exception,
    path: String,
    place: String,
    logger: Logger,
) {
    logger.atError().setMessage(error.message).addKeyValue("DIRECTORY", path).addKeyValue("PLACE", place).log()
    System.err.println("Исключение при считывании данных из директории /$path: ${error.message}")
    System.err.println("Аварийное завершение...")
}

fun regSDHook(
    path: String,
    mapper: ObjectMapper,
    announcementStorage: AnnouncementStorage,
    specialistStorage: SpecialistStorage,
    categoryStorage: CategoryStorage,
    degreeStorage: DegreeStorage,
) = Runtime.getRuntime().addShutdownHook(
    thread(start = false) {
        mapper.writeValue(File("$path/announcements.json"), announcementStorage.getAll())
        println("Объявления сохранены")
        mapper.writeValue(File("$path/specialists.json"), specialistStorage.getAll())
        println("Специалисты сохранены")
        mapper.writeValue(File("$path/categories.json"), categoryStorage.getAll())
        println("Категории сохранены")
        mapper.writeValue(File("$path/degrees.json"), degreeStorage.getAll())
        println("Образования сохранены")
        println("Данные успешно сохранены.")
    },
)

fun main() {
    val appConfig = AppConfig.readConfiguration()
    val jwtTools = JwtTools(appConfig.jwtSalt, "SpecialistFinder")
    val path = appConfig.dataStorage
    val mapper = jacksonObjectMapper()
    mapper.enable(SerializationFeature.INDENT_OUTPUT)
    val loggingFilter = LoggingFilter(MAIN_CLASS)
    val announcementStorage: AnnouncementStorage
    val specialistStorage: SpecialistStorage
    val categoryStorage: CategoryStorage
    val degreeStorage: DegreeStorage
    try {
        announcementStorage =
            StoragesOperationsAndMethods.initAnnouncementStorage(
                mapper.readValue<List<Announcement>>(
                    File("$path/announcements.json"),
                ),
            )
        specialistStorage =
            StoragesOperationsAndMethods.initSpecialistStorage(
                mapper.readValue<List<Specialist>>(
                    File("$path/specialists.json"),
                ),
            )
        categoryStorage =
            StoragesOperationsAndMethods.initCategoryStorage(
                mapper.readValue<List<Category>>(
                    File("$path/categories.json"),
                ),
            )
        degreeStorage =
            StoragesOperationsAndMethods.initDegreeStorage(
                mapper.readValue<List<Degree>>(
                    File("$path/degrees.json"),
                ),
            )
    } catch (ex: Exception) {
        when (ex) {
            is FileNotFoundException,
            is IllegalArgumentException,
            is com.fasterxml.jackson.databind.exc.ValueInstantiationException,
            is com.fasterxml.jackson.core.JsonParseException,
            -> {
                logInitError(ex, path, "CreateStorages", loggingFilter.logger)
                return
            }

            else -> throw ex
        }
    }

    println("Данные взяты из директории /$path")

    val storagesOperations =
        StoragesOperationsAndMethods(
            announcementStorage,
            specialistStorage,
            categoryStorage,
            degreeStorage,
            appConfig.authSalt,
        )
    val categoryLenses = CategoryLenses(storagesOperations)
    val degreeLenses = DegreeLenses(storagesOperations)
    val announcementLenses = AnnouncementLenses(categoryLenses)
    val specialistLenses = SpecialistLenses(degreeLenses)
    val contextTools = ContextTools(ContextAwareViewRender.withContentType(cacheRenderer(), TEXT_HTML))
    val appWithStaticResources =
        loggingFilter.then(ServerFilters.InitialiseRequestContext(contextTools.appContexts))
            .then(ForbiddenFilter(contextTools.htmlViewWithContexts))
            .then(NotFoundFilter(contextTools.htmlViewWithContexts))
            .then(AddCategoriesToContextFilter(contextTools.allCategoriesLens, storagesOperations)).then(
                AddAuthUserToContextFilter(
                    contextTools.userAuthLens,
                    CreateAuthUserByLoginImpl(specialistStorage),
                    jwtTools,
                ),
            ).then(
                AddUserPermissionsToContextFilter(
                    contextTools.userAuthLens,
                    contextTools.userPermissionsLens,
                ),
            ).then(
                routes(
                    router(
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
                    static(ResourceLoader.Classpath("/ru/yarsu/public")),
                ),
            )
//    regSDHook(path, mapper, announcementStorage, specialistStorage, categoryStorage, degreeStorage)
    val server = appWithStaticResources.asServer(Netty(appConfig.webPort)).start()
    println("Сервер запущен на http://localhost:" + server.port())
    println("Нажмите enter чтобы завершить работу.")
    readln()
    println("Завершение...")
    println("Сохранение данных, не выключайте компьютер и не выдергивайте вилку питания из розетки.")
    server.stop()
    return
}
