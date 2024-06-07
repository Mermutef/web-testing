package ru.yarsu.domain.operations.specialist

import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.storages.SpecialistStorage

interface GetSpecialistOperation {
    fun get(id: Int): Specialist?
}

class GetSpecialistOperationImpl(
    private val storage: SpecialistStorage,
) : GetSpecialistOperation {
    override fun get(id: Int): Specialist? {
        return storage.get(id)
    }
}
