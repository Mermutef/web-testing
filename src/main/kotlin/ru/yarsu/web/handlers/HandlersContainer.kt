package ru.yarsu.web.handlers

import org.http4k.core.then
import ru.yarsu.domain.entities.AuthorizationMethods
import ru.yarsu.domain.entities.JwtTools
import ru.yarsu.domain.entities.Permissions
import ru.yarsu.domain.storages.StoragesOperationsAndMethods
import ru.yarsu.web.contexts.ContextTools
import ru.yarsu.web.filters.PermissionsFilter
import ru.yarsu.web.filters.RoleNameFilter
import ru.yarsu.web.handlers.announcement.AnnouncementHandler
import ru.yarsu.web.handlers.announcement.AnnouncementListHandler
import ru.yarsu.web.handlers.announcement.DeleteAnnouncementHandler
import ru.yarsu.web.handlers.announcement.EditAnnouncementHandler
import ru.yarsu.web.handlers.announcement.SaveAnnouncementHandler
import ru.yarsu.web.handlers.announcement.ShowDeleteAnnouncementFormHandler
import ru.yarsu.web.handlers.announcement.ShowNewAnnouncementFormHandler
import ru.yarsu.web.handlers.auth.LoginHandler
import ru.yarsu.web.handlers.auth.LogoutHandler
import ru.yarsu.web.handlers.auth.ShowLoginFormHandler
import ru.yarsu.web.handlers.category.DeleteCategoryHandler
import ru.yarsu.web.handlers.category.EditCategoryHandler
import ru.yarsu.web.handlers.category.SaveCategoryHandler
import ru.yarsu.web.handlers.category.ShowDeleteCategoryFormHandler
import ru.yarsu.web.handlers.category.ShowNewCategoryFormHandler
import ru.yarsu.web.handlers.specialist.DeleteSpecialistHandler
import ru.yarsu.web.handlers.specialist.EditRoleHandler
import ru.yarsu.web.handlers.specialist.EditSpecialistHandler
import ru.yarsu.web.handlers.specialist.SaveSpecialistHandler
import ru.yarsu.web.handlers.specialist.ShowDeleteSpecialistFormHandler
import ru.yarsu.web.handlers.specialist.ShowEditRoleFormHandler
import ru.yarsu.web.handlers.specialist.ShowNewSpecialistFormHandler
import ru.yarsu.web.handlers.specialist.SpecialistHandler
import ru.yarsu.web.handlers.specialist.SpecialistListHandler
import ru.yarsu.web.lenses.AnnouncementLenses
import ru.yarsu.web.lenses.CategoryLenses
import ru.yarsu.web.lenses.DegreeLenses
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.templates.ContextAwareViewRender

class HandlersContainer(
    htmlView: ContextAwareViewRender,
    storagesOperations: StoragesOperationsAndMethods,
    announcementLenses: AnnouncementLenses,
    specialistLenses: SpecialistLenses,
    categoryLenses: CategoryLenses,
    degreeLenses: DegreeLenses,
    jwtTools: JwtTools,
    contextTools: ContextTools,
    authMethods: AuthorizationMethods,
) {
    private val manageUsers =
        PermissionsFilter(
            contextTools.userPermissionsLens,
            Permissions::manageUsers,
        )
    private val manageCategories =
        PermissionsFilter(
            contextTools.userPermissionsLens,
            Permissions::manageCategories,
        )
    private val addAnnouncements =
        PermissionsFilter(
            contextTools.userPermissionsLens,
            Permissions::addAnnouncements,
        )
    private val deleteAnnouncements =
        PermissionsFilter(
            contextTools.userPermissionsLens,
            Permissions::deleteAnnouncements,
        )
    val home = HomeHandler(htmlView)
    val ping = PingHandler()
    val loginForm = ShowLoginFormHandler(htmlView)
    val login =
        LoginHandler(
            htmlView,
            storagesOperations.authorizationSpecialist,
            specialistLenses,
            jwtTools,
        )
    val logout = LogoutHandler()
    val specialistList =
        manageUsers.then(
            SpecialistListHandler(
                htmlView,
                storagesOperations.specialistDateTimeFilter,
            ),
        )
    val newSpecialistForm =
        ShowNewSpecialistFormHandler(
            htmlView,
            storagesOperations.getMainDegrees,
        )
    val saveSpecialist =
        SaveSpecialistHandler(
            htmlView,
            contextTools.userAuthLens,
            storagesOperations.updateSpecialist,
            storagesOperations.addDegree,
            storagesOperations.getMainDegrees,
            storagesOperations.checkUniquenessOfPassword,
            specialistLenses,
            degreeLenses,
        )
    val specialist =
        RoleNameFilter(
            storagesOperations.getSpecialist,
            contextTools.roleNameLens,
        ).then(
            SpecialistHandler(
                htmlView,
                authMethods,
                storagesOperations.getSpecialist,
                storagesOperations.encodeDegrees,
                contextTools.userAuthLens,
            ),
        )
    val editSpecialistForm =
        EditSpecialistHandler(
            htmlView,
            contextTools.userAuthLens,
            storagesOperations.encodeDegrees,
            storagesOperations.getMainDegrees,
            degreeLenses,
        )
    val editSpecialist =
        SaveSpecialistHandler(
            htmlView,
            contextTools.userAuthLens,
            storagesOperations.updateSpecialist,
            storagesOperations.addDegree,
            storagesOperations.getMainDegrees,
            storagesOperations.checkUniquenessOfPassword,
            specialistLenses,
            degreeLenses,
        )
    val editRoleForm =
        manageUsers.then(
            ShowEditRoleFormHandler(
                htmlView,
                storagesOperations.getSpecialist,
            ),
        )
    val editRole =
        manageUsers.then(
            EditRoleHandler(
                htmlView,
                storagesOperations.getSpecialist,
                storagesOperations.editSpecialist,
                specialistLenses,
            ),
        )
    val deleteSpecialistForm =
        manageUsers.then(
            ShowDeleteSpecialistFormHandler(htmlView),
        )
    val deleteSpecialist =
        manageUsers.then(
            DeleteSpecialistHandler(
                htmlView,
                storagesOperations.getSpecialist,
                storagesOperations.clearSpecialistAnnouncements,
                storagesOperations.deleteSpecialist,
            ),
        )
    val newCategoryForm = manageCategories.then(ShowNewCategoryFormHandler(htmlView))
    val saveCategory =
        manageCategories.then(
            SaveCategoryHandler(
                htmlView,
                storagesOperations.updateCategory,
            ),
        )
    val announcementList =
        AnnouncementListHandler(
            htmlView,
            storagesOperations.getCategory,
            storagesOperations.announcementDateTimeFilter,
            storagesOperations::specialistsByCategory,
        )
    val editCategory =
        manageCategories.then(
            EditCategoryHandler(
                htmlView,
                storagesOperations.getCategory,
                categoryLenses,
            ),
        )
    val deleteCategoryForm = manageCategories.then(ShowDeleteCategoryFormHandler(htmlView))
    val deleteCategory =
        manageCategories.then(
            DeleteCategoryHandler(
                htmlView,
                storagesOperations.clearCategoryAnnouncements,
                storagesOperations.getCategory,
                storagesOperations.deleteCategory,
            ),
        )
    val newAnnouncementForm =
        addAnnouncements.then(
            ShowNewAnnouncementFormHandler(
                htmlView,
                authMethods,
                contextTools.userAuthLens,
            ),
        )
    val saveAnnouncement =
        addAnnouncements.then(
            SaveAnnouncementHandler(
                htmlView,
                authMethods,
                storagesOperations.updateAnnouncement,
                announcementLenses,
                categoryLenses,
                contextTools.userAuthLens,
            ),
        )
    val announcement =
        AnnouncementHandler(
            htmlView,
            storagesOperations.getAnnouncement,
            storagesOperations.getSpecialist,
            storagesOperations.getCategory,
            storagesOperations.encodeDegrees,
            contextTools.userAuthLens,
        )
    val editAnnouncement =
        EditAnnouncementHandler(
            htmlView,
            authMethods,
            contextTools.userAuthLens,
            storagesOperations.getAnnouncement,
            categoryLenses,
        )
    val deleteAnnouncementForm = deleteAnnouncements.then(ShowDeleteAnnouncementFormHandler(htmlView))
    val deleteAnnouncement =
        deleteAnnouncements.then(
            DeleteAnnouncementHandler(
                htmlView,
                storagesOperations.getAnnouncement,
                storagesOperations.deleteAnnouncement,
            ),
        )
}
