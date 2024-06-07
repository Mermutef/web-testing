package ru.yarsu.domain.operations.announcement

import ru.yarsu.domain.entities.Announcement
import ru.yarsu.domain.storages.AnnouncementStorage

interface GetAnnouncementOperation {
    fun get(id: Int): Announcement?
}

class GetAnnouncementOperationImpl(
    private val storage: AnnouncementStorage,
) : GetAnnouncementOperation {
    override fun get(id: Int): Announcement? {
        return storage.get(id)
    }
}
