package ru.yarsu.domain.operations.announcement

import ru.yarsu.domain.storages.AnnouncementStorage

interface DeleteAnnouncementOperation {
    fun delete(id: Int): Boolean
}

class DeleteAnnouncementOperationImpl(
    private val storage: AnnouncementStorage,
) : DeleteAnnouncementOperation {
    override fun delete(id: Int): Boolean = storage.delete(id)
}
