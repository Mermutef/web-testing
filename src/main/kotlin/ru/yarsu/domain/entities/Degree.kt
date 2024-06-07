package ru.yarsu.domain.entities

data class Degree(
    val id: Int,
    val type: String,
    val ru: String,
) : Comparable<Degree> {
    override fun compareTo(other: Degree): Int =
        this.ru.compareTo(other.ru, ignoreCase = true) + this.type.compareTo(other.type, ignoreCase = true)
}
