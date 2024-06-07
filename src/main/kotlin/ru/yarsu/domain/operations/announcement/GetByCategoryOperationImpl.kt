package ru.yarsu.domain.operations.announcement

import ru.yarsu.domain.entities.Announcement
import ru.yarsu.domain.storages.AnnouncementStorage

interface GetByCategoryOperation {
    fun getByCategory(id: Int): List<Announcement>
}

class GetByCategoryOperationImpl(val storage: AnnouncementStorage) : GetByCategoryOperation {
    override fun getByCategory(id: Int): List<Announcement> = storage.getAll().filter { it.category == id }
}
