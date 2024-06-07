package ru.yarsu.domain.storages

import ru.yarsu.domain.entities.Category

class CategoryStorage(
    categories: List<Category>,
) {
    private val storage: MutableMap<Int, Category>
    private var firstFree: Int

    init {
        require(categories.distinctBy { it.id }.size == categories.size) {
            "Обнаружено дублирование id в базе категорий. Исправьте данные и повторите попытку."
        }
        storage = categories.associateBy({ it.id }, { it }).toMutableMap()
        firstFree = storage.values.maxOf { it.id } + 1
    }

    fun get(id: Int): Category? = storage[id]

    fun getAll(): List<Category> = storage.values.toList()

    fun add(category: Category): Int {
        val existsCategory = findCategory(category)
        if (existsCategory != null) {
            return existsCategory
        }
        storage[firstFree] = category.copy(id = firstFree)
        ++firstFree
        return firstFree - 1
    }

    fun findCategory(category: Category): Int? {
        storage.values.forEach { if (it == category) return it.id }
        return null
    }

    @Synchronized
    fun edit(category: Category): Boolean {
        if (storage.values.map { it.id }.contains(category.id)) {
            storage[category.id] = category
            return true
        }
        return false
    }

    @Synchronized
    fun delete(id: Int): Boolean {
        if (storage.values.map { it.id }.contains(id)) {
            storage.remove(id)
            return true
        }
        return false
    }
}
