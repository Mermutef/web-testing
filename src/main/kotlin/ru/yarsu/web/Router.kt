package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.web.handlers.HandlersContainer

fun router(handlers: HandlersContainer): RoutingHttpHandler =
    routes(
        "/" bind Method.GET to handlers.home,
        "/ping" bind Method.GET to handlers.ping,
        "/login" bind Method.GET to handlers.loginForm,
        "/login" bind Method.POST to handlers.login,
        "/logout" bind Method.GET to handlers.logout,
        "/users" bind Method.GET to handlers.specialistList,
        "/users/new" bind Method.GET to handlers.newSpecialistForm,
        "/users/new" bind Method.POST to handlers.saveSpecialist,
        "/users/{id}" bind Method.GET to handlers.specialist,
        "/users/{id}/edit" bind Method.GET to handlers.editSpecialistForm,
        "/users/{id}/edit" bind Method.POST to handlers.editSpecialist,
        "/users/{id}/edit-role" bind Method.GET to handlers.editRoleForm,
        "/users/{id}/edit-role" bind Method.POST to handlers.editRole,
        "/users/{id}/delete" bind Method.GET to handlers.deleteSpecialistForm,
        "/users/{id}/delete" bind Method.POST to handlers.deleteSpecialist,
        "/categories/new" bind Method.GET to handlers.newCategoryForm,
        "/categories/new" bind Method.POST to handlers.saveCategory,
        "/categories/{id}" bind Method.GET to handlers.announcementList,
        "/categories/{id}/edit" bind Method.GET to handlers.editCategory,
        "/categories/{id}/edit" bind Method.POST to handlers.saveCategory,
        "/categories/{id}/delete" bind Method.GET to handlers.deleteCategoryForm,
        "/categories/{id}/delete" bind Method.POST to handlers.deleteCategory,
        "/announcements/new" bind Method.GET to handlers.newAnnouncementForm,
        "/announcements/new" bind Method.POST to handlers.saveAnnouncement,
        "/announcements/{id}" bind Method.GET to handlers.announcement,
        "/announcements/{id}/edit" bind Method.GET to handlers.editAnnouncement,
        "/announcements/{id}/edit" bind Method.POST to handlers.saveAnnouncement,
        "/announcements/{id}/delete" bind Method.GET to handlers.deleteAnnouncementForm,
        "/announcements/{id}/delete" bind Method.POST to handlers.deleteAnnouncement,
    )
