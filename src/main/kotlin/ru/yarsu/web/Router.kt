package ru.yarsu.web

import org.http4k.core.Method
import org.http4k.core.then
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import ru.yarsu.domain.entities.AuthorizationMethods
import ru.yarsu.domain.entities.JwtTools
import ru.yarsu.domain.entities.Permissions
import ru.yarsu.domain.storages.StoragesOperationsAndMethods
import ru.yarsu.web.contexts.ContextTools
import ru.yarsu.web.filters.AddCanEditAnnouncementPermissionToContextFilter
import ru.yarsu.web.filters.AddCanEditUserPermissionToContextFilter
import ru.yarsu.web.filters.AddRoleNameToContextFilter
import ru.yarsu.web.filters.PermissionsFilter
import ru.yarsu.web.filters.SetEditModeFilter
import ru.yarsu.web.handlers.HomeHandler
import ru.yarsu.web.handlers.PingHandler
import ru.yarsu.web.handlers.announcement.AnnouncementHandler
import ru.yarsu.web.handlers.announcement.AnnouncementListHandler
import ru.yarsu.web.handlers.announcement.DeleteAnnouncementHandler
import ru.yarsu.web.handlers.announcement.EditAnnouncementHandler
import ru.yarsu.web.handlers.announcement.SaveAnnouncementHandler
import ru.yarsu.web.handlers.announcement.ShowDeleteAnnouncementFormHandler
import ru.yarsu.web.handlers.announcement.ShowNewAnnouncementFormHandler
import ru.yarsu.web.handlers.category.DeleteCategoryHandler
import ru.yarsu.web.handlers.category.EditCategoryHandler
import ru.yarsu.web.handlers.category.SaveCategoryHandler
import ru.yarsu.web.handlers.category.ShowDeleteCategoryFormHandler
import ru.yarsu.web.handlers.category.ShowNewCategoryFormHandler
import ru.yarsu.web.handlers.specialist.DeleteSpecialistHandler
import ru.yarsu.web.handlers.specialist.EditRoleHandler
import ru.yarsu.web.handlers.specialist.EditSpecialistHandler
import ru.yarsu.web.handlers.specialist.LoginHandler
import ru.yarsu.web.handlers.specialist.LogoutHandler
import ru.yarsu.web.handlers.specialist.SaveSpecialistHandler
import ru.yarsu.web.handlers.specialist.ShowDeleteSpecialistFormHandler
import ru.yarsu.web.handlers.specialist.ShowEditRoleFormHandler
import ru.yarsu.web.handlers.specialist.ShowLoginFormHandler
import ru.yarsu.web.handlers.specialist.ShowNewSpecialistFormHandler
import ru.yarsu.web.handlers.specialist.SpecialistHandler
import ru.yarsu.web.handlers.specialist.SpecialistListHandler
import ru.yarsu.web.lenses.AnnouncementLenses
import ru.yarsu.web.lenses.CategoryLenses
import ru.yarsu.web.lenses.DegreeLenses
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.templates.ContextAwareViewRender

fun router(
    htmlView: ContextAwareViewRender,
    storagesOperations: StoragesOperationsAndMethods,
    announcementLenses: AnnouncementLenses,
    specialistLenses: SpecialistLenses,
    categoryLenses: CategoryLenses,
    degreeLenses: DegreeLenses,
    jwtTools: JwtTools,
    contextTools: ContextTools,
    authMethods: AuthorizationMethods,
): RoutingHttpHandler =
    routes(
        "/" bind Method.GET to HomeHandler(htmlView),
        "/ping" bind Method.GET to PingHandler(),
        "/login" bind Method.GET to ShowLoginFormHandler(htmlView),
        "/login" bind Method.POST to
            LoginHandler(
                htmlView,
                storagesOperations.authorizationSpecialist,
                jwtTools,
            ),
        "/logout" bind Method.GET to LogoutHandler(),
        "/users" bind Method.GET to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::manageUsers,
            ).then(
                SpecialistListHandler(
                    htmlView,
                    storagesOperations.specialistDateTimeFilter,
                ),
            ),
        "/users/new" bind Method.GET to
            ShowNewSpecialistFormHandler(
                htmlView,
                storagesOperations.getMainDegrees,
            ),
        "/users/new" bind Method.POST to
            SaveSpecialistHandler(
                htmlView,
                contextTools.userAuthLens,
                storagesOperations.getSpecialist,
                storagesOperations.updateSpecialist,
                storagesOperations.addDegree,
                storagesOperations.getMainDegrees,
                storagesOperations.checkUniquenessOfPassword,
                specialistLenses,
                degreeLenses,
                storagesOperations.salt,
            ),
        "/users/{id}" bind Method.GET to
            AddCanEditUserPermissionToContextFilter(
                storagesOperations.getSpecialist,
                contextTools.userAuthLens,
                contextTools.canEditLens,
            ).then(
                AddRoleNameToContextFilter(
                    storagesOperations.getSpecialist,
                    contextTools.roleNameLens,
                ),
            )
                .then(
                    SpecialistHandler(
                        htmlView,
                        authMethods,
                        storagesOperations.getSpecialist,
                        storagesOperations.encodeDegrees,
                        contextTools.userAuthLens,
                    ),
                ),
        "/users/{id}/edit" bind Method.GET to
            SetEditModeFilter(contextTools.editModeLens).then(
                EditSpecialistHandler(
                    htmlView,
                    contextTools.userAuthLens,
                    storagesOperations.getSpecialist,
                    storagesOperations.encodeDegrees,
                    storagesOperations.getMainDegrees,
                    degreeLenses,
                ),
            ),
        "/users/{id}/edit" bind Method.POST to
            SetEditModeFilter(contextTools.editModeLens).then(
                SaveSpecialistHandler(
                    htmlView,
                    contextTools.userAuthLens,
                    storagesOperations.getSpecialist,
                    storagesOperations.updateSpecialist,
                    storagesOperations.addDegree,
                    storagesOperations.getMainDegrees,
                    storagesOperations.checkUniquenessOfPassword,
                    specialistLenses,
                    degreeLenses,
                    storagesOperations.salt,
                ),
            ),
        "/users/{id}/edit-role" bind Method.GET to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::manageUsers,
            ).then(
                ShowEditRoleFormHandler(
                    htmlView,
                    storagesOperations.getSpecialist,
                ),
            ),
        "/users/{id}/edit-role" bind Method.POST to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::manageUsers,
            ).then(
                EditRoleHandler(
                    htmlView,
                    storagesOperations.getSpecialist,
                    storagesOperations.editSpecialist,
                ),
            ),
        "/users/{id}/delete" bind Method.GET to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::manageUsers,
            ).then(
                ShowDeleteSpecialistFormHandler(htmlView),
            ),
        "/users/{id}/delete" bind Method.POST to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::manageUsers,
            ).then(
                DeleteSpecialistHandler(
                    htmlView,
                    storagesOperations.getSpecialist,
                    storagesOperations.clearSpecialistAnnouncements,
                    storagesOperations.deleteSpecialist,
                ),
            ),
        "/categories/new" bind Method.GET to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::manageCategories,
            ).then(ShowNewCategoryFormHandler(htmlView)),
        "/categories/new" bind Method.POST to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::manageCategories,
            ).then(
                SaveCategoryHandler(
                    htmlView,
                    storagesOperations.updateCategory,
                ),
            ),
        "/categories/{id}" bind Method.GET to
            AnnouncementListHandler(
                htmlView,
                storagesOperations.getCategory,
                storagesOperations.announcementDateTimeFilter,
                storagesOperations::specialistsByCategory,
            ),
        "/categories/{id}/edit" bind Method.GET to
            SetEditModeFilter(contextTools.editModeLens).then(
                PermissionsFilter(
                    contextTools.userPermissionsLens,
                    Permissions::manageCategories,
                ),
            ).then(
                EditCategoryHandler(
                    htmlView,
                    storagesOperations.getCategory,
                    categoryLenses,
                ),
            ),
        "/categories/{id}/edit" bind Method.POST to
            SetEditModeFilter(contextTools.editModeLens).then(
                PermissionsFilter(
                    contextTools.userPermissionsLens,
                    Permissions::manageCategories,
                ),
            ).then(
                SaveCategoryHandler(
                    htmlView,
                    storagesOperations.updateCategory,
                ),
            ),
        "/categories/{id}/delete" bind Method.GET to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::manageCategories,
            ).then(ShowDeleteCategoryFormHandler(htmlView)),
        "/categories/{id}/delete" bind Method.POST to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::manageCategories,
            ).then(
                DeleteCategoryHandler(
                    htmlView,
                    storagesOperations.clearCategoryAnnouncements,
                    storagesOperations.getCategory,
                    storagesOperations.deleteCategory,
                ),
            ),
        "/announcements/new" bind Method.GET to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::addAnnouncements,
            ).then(
                ShowNewAnnouncementFormHandler(
                    htmlView,
                    authMethods,
                    contextTools.userAuthLens,
                ),
            ),
        "/announcements/new" bind Method.POST to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::addAnnouncements,
            ).then(
                SaveAnnouncementHandler(
                    htmlView,
                    authMethods,
                    storagesOperations.updateAnnouncement,
                    announcementLenses,
                    categoryLenses,
                    contextTools.userAuthLens,
                ),
            ),
        "/announcements/{id}" bind Method.GET to
            AddCanEditAnnouncementPermissionToContextFilter(
                storagesOperations.getAnnouncement,
                contextTools.userAuthLens,
                contextTools.canEditLens,
            ).then(
                AnnouncementHandler(
                    htmlView,
                    storagesOperations.getAnnouncement,
                    storagesOperations.getSpecialist,
                    storagesOperations.getCategory,
                    storagesOperations.encodeDegrees,
                ),
            ),
        "/announcements/{id}/edit" bind Method.GET to
            SetEditModeFilter(contextTools.editModeLens).then(
                EditAnnouncementHandler(
                    htmlView,
                    authMethods,
                    contextTools.userAuthLens,
                    storagesOperations.getAnnouncement,
                    categoryLenses,
                ),
            ),
        "/announcements/{id}/edit" bind Method.POST to
            SetEditModeFilter(contextTools.editModeLens).then(
                SaveAnnouncementHandler(
                    htmlView,
                    authMethods,
                    storagesOperations.updateAnnouncement,
                    announcementLenses,
                    categoryLenses,
                    contextTools.userAuthLens,
                ),
            ),
        "/announcements/{id}/delete" bind Method.GET to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::deleteAnnouncements,
            ).then(ShowDeleteAnnouncementFormHandler(htmlView)),
        "/announcements/{id}/delete" bind Method.POST to
            PermissionsFilter(
                contextTools.userPermissionsLens,
                Permissions::deleteAnnouncements,
            ).then(
                DeleteAnnouncementHandler(
                    htmlView,
                    storagesOperations.getAnnouncement,
                    storagesOperations.deleteAnnouncement,
                ),
            ),
    )
