package ru.yarsu.domain.operations.specialist

import ru.yarsu.domain.entities.AuthUser
import ru.yarsu.domain.entities.Permissions
import ru.yarsu.domain.storages.SpecialistStorage

interface CreateAuthUserByLogin {
    fun create(login: String): AuthUser?
}

class CreateAuthUserByLoginImpl(
    private val storage: SpecialistStorage,
) : CreateAuthUserByLogin {
    override fun create(login: String): AuthUser? =
        storage.getByLogin(login)?.let {
            AuthUser(
                it.login,
                it.id,
                Permissions.rolePermissionsById(it.permissions),
            )
        }
}
