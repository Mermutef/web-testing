package ru.yarsu.domain.entities

const val GUEST_ID = 0
const val SPECIALIST_ID = 1
const val MODERATOR_ID = 2
const val ADMINISTRATOR_ID = 3

data class Permissions(
    val id: Int,
    val manageUsers: Boolean = false,
    val addAnnouncements: Boolean = false,
    val deleteAnnouncements: Boolean = false,
    val manageCategories: Boolean = false,
) {
    companion object {
        val GUEST = Permissions(id = GUEST_ID)
        val SPECIALIST = Permissions(id = SPECIALIST_ID, addAnnouncements = true)
        val MODERATOR = SPECIALIST.copy(id = MODERATOR_ID, manageCategories = true, deleteAnnouncements = true)
        val ADMIN = MODERATOR.copy(id = ADMINISTRATOR_ID, manageUsers = true)

        fun rolePermissionsById(id: Int): Permissions {
            return when (id) {
                SPECIALIST_ID -> SPECIALIST
                MODERATOR_ID -> MODERATOR
                ADMINISTRATOR_ID -> ADMIN
                else -> GUEST
            }
        }

        fun roleNameById(id: Int): String {
            return when (id) {
                SPECIALIST_ID -> "Специалист"
                MODERATOR_ID -> "Модератор"
                ADMINISTRATOR_ID -> "Администратор"
                else -> "Гость"
            }
        }
    }
}
