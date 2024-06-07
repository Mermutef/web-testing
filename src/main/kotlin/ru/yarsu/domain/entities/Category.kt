package ru.yarsu.domain.entities

data class Category(
    val id: Int,
    val ru: String,
    val needLicense: Boolean,
) : Comparable<Category> {
    override fun toString(): String = ru

    override fun compareTo(other: Category): Int = this.ru.compareTo(other.ru)
}
