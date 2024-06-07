package ru.yarsu.domain.operations.announcement

import ru.yarsu.domain.storages.AnnouncementStorage

interface ClearCategoryAnnouncementsOperation {
    fun clear(categoryId: Int): Boolean
}

class ClearCategoryAnnouncementsOperationImpl(
    private val announcementStorage: AnnouncementStorage,
) : ClearCategoryAnnouncementsOperation {
    override fun clear(categoryId: Int): Boolean {
        val categoryAnnouncements = announcementStorage.getAll().filter { it.category == categoryId }
        var successFlag = true

        for (announcement in categoryAnnouncements) {
            if (!announcementStorage.delete(announcement.id)) {
                successFlag = false
            }
        }
        return successFlag
    }
}
