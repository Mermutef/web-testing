package ru.yarsu.domain.operations.specialist

import ru.yarsu.domain.storages.SpecialistStorage

interface DeleteSpecialistOperation {
    fun delete(id: Int): Boolean
}

class DeleteSpecialistOperationImpl(
    private val storage: SpecialistStorage,
) : DeleteSpecialistOperation {
    override fun delete(id: Int): Boolean = storage.delete(id)
}
