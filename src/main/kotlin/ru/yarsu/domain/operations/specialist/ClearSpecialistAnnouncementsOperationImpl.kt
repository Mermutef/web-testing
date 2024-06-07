package ru.yarsu.domain.operations.specialist

import ru.yarsu.domain.storages.AnnouncementStorage

interface ClearSpecialistAnnouncementsOperation {
    fun clear(specialistId: Int): Boolean
}

class ClearSpecialistAnnouncementsOperationImpl(
    private val announcementStorage: AnnouncementStorage,
) : ClearSpecialistAnnouncementsOperation {
    override fun clear(specialistId: Int): Boolean {
        val specialistAnnouncements = announcementStorage.getAll().filter { it.specialist == specialistId }
        var successFlag = true

        for (announcement in specialistAnnouncements) {
            if (!announcementStorage.delete(announcement.id)) {
                successFlag = false
            }
        }
        return successFlag
    }
}
