package ru.yarsu.domain.operations.specialist

import ru.yarsu.domain.entities.EntitiesCheckAndHelpMethods
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.storages.SpecialistStorage

interface AuthorizationSpecialistOperation {
    fun auth(
        login: String,
        password: String,
    ): Specialist?
}

class AuthorizationSpecialistOperationImpl(
    private val specialistStorage: SpecialistStorage,
    private val salt: String,
) : AuthorizationSpecialistOperation {
    override fun auth(
        login: String,
        password: String,
    ): Specialist? {
        val specialist = specialistStorage.getByLogin(login) ?: return null
        if (EntitiesCheckAndHelpMethods.saltPassword(password, salt) != specialist.password) {
            return null
        }
        return specialist
    }
}
