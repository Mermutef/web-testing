package ru.yarsu.domain.entities

import ru.yarsu.domain.storages.StoragesOperationsAndMethods

class AuthorizationMethods(private val storagesOperations: StoragesOperationsAndMethods) {
    fun authAddOrEditAnnouncement(
        user: Specialist?,
        announcementId: Int?,
    ): Boolean {
        if (user == null) return false
        if (!Permissions.rolePermissionsById(user.permissions).addAnnouncements) return false
        if (announcementId == null) {
            return true
        }
        val authorId = storagesOperations.getSpecialistId.getByAnnouncementId(announcementId) ?: return false
        return authorId == user.id
    }

    fun authSeeUserInfo(
        specialist: Specialist?,
        neededUser: Int,
    ): Boolean {
        if (specialist == null) return false
        return specialist.id == neededUser || Permissions.rolePermissionsById(specialist.permissions).manageUsers
    }
}
