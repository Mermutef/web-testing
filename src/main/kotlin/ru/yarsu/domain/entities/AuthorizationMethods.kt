package ru.yarsu.domain.entities

import ru.yarsu.domain.storages.StoragesOperationsAndMethods

class AuthorizationMethods(private val storagesOperations: StoragesOperationsAndMethods) {
    fun authAddOrEditAnnouncement(
        user: AuthUser?,
        announcementId: Int?,
    ): Boolean {
        if (user == null) return false
        if (!user.permissions.addAnnouncements) return false
        if (announcementId == null) {
            return true
        }
        val authorId = storagesOperations.getSpecialistId.getByAnnouncementId(announcementId) ?: return false
        return authorId == user.id
    }

    fun authSeeUserInfo(
        user: AuthUser?,
        neededUser: Int,
    ): Boolean {
        if (user == null) return false
        return user.id == neededUser || user.permissions.manageUsers
    }
}
