package ru.yarsu.domain.entities

data class AuthUser(
    val login: String,
    val id: Int,
    val permissions: Permissions,
)
