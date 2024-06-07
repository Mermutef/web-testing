package ru.yarsu.domain.operations.announcement

import ru.yarsu.domain.storages.AnnouncementStorage

interface GetSpecialistIdOperation {
    fun getByAnnouncementId(id: Int): Int?
}

class GetSpecialistIdOperationImpl(private val storage: AnnouncementStorage) : GetSpecialistIdOperation {
    override fun getByAnnouncementId(id: Int): Int? = storage.get(id)?.specialist
}
