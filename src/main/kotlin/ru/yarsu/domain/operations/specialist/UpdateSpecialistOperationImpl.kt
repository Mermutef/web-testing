package ru.yarsu.domain.operations.specialist

import ru.yarsu.domain.entities.EntitiesCheckAndHelpMethods
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.storages.SpecialistStorage

interface UpdateSpecialistOperation {
    fun update(specialist: Specialist): Int
}

class UpdateSpecialistOperationImpl(
    private val storage: SpecialistStorage,
    private val salt: String,
) :
    UpdateSpecialistOperation {
    override fun update(specialist: Specialist): Int {
        val saltedPassword = EntitiesCheckAndHelpMethods.saltPassword(specialist.password, salt)
        if (specialist.id != -1) {
            storage.edit(specialist.copy(password = saltedPassword))
            return specialist.id
        }
        return storage.add(specialist.copy(password = saltedPassword))
    }
}
