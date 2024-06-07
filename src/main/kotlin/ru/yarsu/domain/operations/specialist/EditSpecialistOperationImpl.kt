package ru.yarsu.domain.operations.specialist

import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.storages.SpecialistStorage

interface EditSpecialistOperation {
    fun edit(specialist: Specialist): Boolean
}

class EditSpecialistOperationImpl(
    private val storage: SpecialistStorage,
) : EditSpecialistOperation {
    override fun edit(specialist: Specialist): Boolean = storage.edit(specialist)
}
