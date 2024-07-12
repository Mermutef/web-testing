package ru.yarsu.domain.operations.specialist

import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.storages.SpecialistStorage

interface UpdateSpecialistOperation {
    fun update(specialist: Specialist): Int
}

class UpdateSpecialistOperationImpl(private val storage: SpecialistStorage) :
    UpdateSpecialistOperation {
    override fun update(specialist: Specialist): Int {
        if (specialist.id != -1) {
            storage.edit(specialist)
            return specialist.id
        }
        return storage.add(specialist)
    }
}
