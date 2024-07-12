package ru.yarsu.web.handlers.announcement

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.http4k.core.*
import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.core.body.form
import org.http4k.filter.ServerFilters
import org.http4k.lens.BiDiBodyLens
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.ViewModel
import org.mockito.kotlin.*
import ru.yarsu.domain.entities.Announcement
import ru.yarsu.domain.operations.announcement.DeleteAnnouncementOperation
import ru.yarsu.domain.operations.announcement.GetAnnouncementOperation
import ru.yarsu.web.contexts.ContextTools
import ru.yarsu.web.models.DeleteAnnouncementVM
import ru.yarsu.web.templates.ContextAwareViewRender
import ru.yarsu.web.templates.cacheRenderer
import java.time.LocalDateTime

/**
 * Поля формы:
 * * idCheck - id удаляемого объявления
 * * deleteAgree - чекбокс соглашения с удалением ("on" - нажат, "" или нет - не нажат)
 * При удалении обязательно должно проверяться:
 * 1. Пользователь согласился с удалением
 * 2. Передан id существующего объявления
 */

class DeleteAnnouncementHandlerTest : FunSpec({
    isolationMode = IsolationMode.InstancePerTest

    val tools = ContextTools(ContextAwareViewRender.withContentType(cacheRenderer(), TEXT_HTML))
    val renderMock: ContextAwareViewRender = mock()
    val lensMock: BiDiBodyLens<ViewModel> = mock()

    whenever(renderMock.invoke(any())).thenReturn(lensMock)
    whenever(lensMock.of<Response>(any())).thenReturn { it }

    val validAnnouncement = Announcement(1, LocalDateTime.now(), 1, "Test", "Test", 1)

    lateinit var announcements: MutableMap<Int, Announcement>

    lateinit var mockDeleteAnnouncement: DeleteAnnouncementOperation
    lateinit var router: RoutingHttpHandler
    lateinit var handler: HttpHandler

    beforeEach {
        announcements = mutableMapOf(validAnnouncement.id to validAnnouncement)

        val mockGetAnnouncement =
            spy(
                object : GetAnnouncementOperation {
                    override fun get(id: Int): Announcement? = announcements[id]
                },
            )

        mockDeleteAnnouncement =
            spy(
                object : DeleteAnnouncementOperation {
                    override fun delete(id: Int): Boolean =
                        announcements[id]?.let {
                            announcements.remove(id)
                            true
                        } ?: false
                },
            )

        router =
            routes(
                "/announcements/{id}/delete" bind Method.POST to
                        DeleteAnnouncementHandler(
                            htmlView = renderMock,
                            getAnnouncement = mockGetAnnouncement,
                            deleteAnnouncement = mockDeleteAnnouncement,
                        ),
            )

        handler =
            ServerFilters
                .InitialiseRequestContext(tools.appContexts)
                .then(router)
    }

    test("Должен возвращать ошибку при неверном ID в форме") {
        val request =
            Request(Method.POST, "/announcements/1/delete")
                .form("idCheck", "2")
                .form("deleteAgree", "on")
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.status shouldBe Status.OK
        verify(lensMock).of<Response>(
            check<DeleteAnnouncementVM> { model ->
                model.form shouldNotBe null
                model.form!!.errors.shouldNotBeEmpty()
            },
        )
    }

    test("Не должен удалять объявление при неверном ID в форме") {
        val request =
            Request(Method.POST, "/announcements/1/delete")
                .form("idCheck", "2")
                .form("deleteAgree", "on")
                .header("content-type", "application/x-www-form-urlencoded")

        handler(request)

        verify(mockDeleteAnnouncement, never()).delete(any())
    }

    test("Должен возвращать NOT_FOUND при удалении несуществующего объявления") {
        val firstFree = announcements.keys.max() + 1
        val request =
            Request(Method.POST, "/announcements/$firstFree/delete")
                .form("idCheck", "$firstFree")
                .form("deleteAgree", "on")
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.status shouldBe Status.NOT_FOUND
    }

    test("Должен успешно удалять объявление при правильных данных") {
        val request =
            Request(Method.POST, "/announcements/${validAnnouncement.id}/delete")
                .form("idCheck", "${validAnnouncement.id}")
                .form("deleteAgree", "on")
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.status shouldBe Status.FOUND
        response.header("Location") shouldBe "/"
        verify(mockDeleteAnnouncement, times(1)).delete(validAnnouncement.id)
    }

    test("Должен возвращать форму при отсутствии согласия на удаление") {
        val request =
            Request(Method.POST, "/announcements/${validAnnouncement.id}/delete")
                .form("idCheck", "${validAnnouncement.id}")
                .header("content-type", "application/x-www-form-urlencoded")

        val response = handler(request)

        response.status shouldBe Status.OK
        response.status shouldBe Status.OK
        verify(lensMock).of<Response>(
            check<DeleteAnnouncementVM> { model ->
                model.form shouldNotBe null
                model.form!!.errors.shouldNotBeEmpty()
            },
        )
    }
})
