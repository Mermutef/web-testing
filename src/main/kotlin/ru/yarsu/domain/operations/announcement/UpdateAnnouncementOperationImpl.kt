package ru.yarsu.domain.operations.announcement

import ru.yarsu.domain.entities.Announcement
import ru.yarsu.domain.storages.AnnouncementStorage

interface UpdateAnnouncementOperation {
    fun update(announcement: Announcement): Int
}

class UpdateAnnouncementOperationImpl(private val announcementStorage: AnnouncementStorage) :
    UpdateAnnouncementOperation {
    override fun update(announcement: Announcement): Int {
        if (announcement.id != -1) {
            announcementStorage.edit(announcement)
            return announcement.id
        }
        return announcementStorage.add(announcement)
    }
}
