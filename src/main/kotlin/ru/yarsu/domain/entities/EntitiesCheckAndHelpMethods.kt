package ru.yarsu.domain.entities

import java.security.MessageDigest

const val MIN_PASSWORD_LENGTH = 8
const val MIN_LOGIN_LENGTH = 2
const val MAX_LOGIN_LENGTH = 20

object EntitiesCheckAndHelpMethods {
    fun checkFCS(fcs: String): Boolean = fcs.isNotBlank()

    fun checkPhone(phone: String): Boolean = phone.isNotBlank() && phone.all { it.isDigit() }

    fun checkSocialId(id: String): Boolean = id.all { it.isDigit() || it == '-' || it.isLetter() }

    fun checkLogin(login: String): Boolean = login.length in MIN_LOGIN_LENGTH..MAX_LOGIN_LENGTH

    fun checkPassword(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH

    fun phoneFormat(phone: String): String =
        phone.removePrefix("+").filterNot { it == ' ' || it == '-' }
            .replaceFirstChar { if (it == '8') '7' else it }

    fun saltPassword(
        password: String,
        salt: String,
    ): String =
        MessageDigest
            .getInstance("SHA-256")
            .digest("${password}$salt".toByteArray())
            .decodeToString()
}
