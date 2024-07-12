package ru.yarsu.domain.operations.specialist

import ru.yarsu.domain.storages.SpecialistStorage

interface CheckUniquenessOfLoginOperation {
    fun checkUniqueness(login: String): Boolean
}

class CheckUniquenessOfPasswordOperationImpl(
    private val specialistStorage: SpecialistStorage,
) : CheckUniquenessOfLoginOperation {
    override fun checkUniqueness(login: String): Boolean {
        return specialistStorage.getAll().map { it.login }.none { it == login }
    }
}
