package ru.yarsu.domain.storages

import ru.yarsu.domain.entities.Degree

class DegreeStorage(
    degrees: List<Degree>,
) {
    private val storage: MutableMap<Int, Degree>
    private var firstFree: Int

    init {
        require(degrees.distinctBy { it.id }.size == degrees.size) {
            "Обнаружено дублирование id в базе образований. Исправьте данные и повторите попытку."
        }
        storage = degrees.associateBy({ it.id }, { it }).toMutableMap()
        firstFree = storage.values.maxOf { it.id } + 1
    }

    fun get(id: Int): Degree? = storage[id]

    fun getAll(): List<Degree> = storage.values.toList()

    fun add(degree: Degree): Int {
        val existsDegree = findDegree(degree)
        if (existsDegree != null) {
            return existsDegree
        }
        storage[firstFree] = degree.copy(id = firstFree)
        ++firstFree
        return firstFree - 1
    }

    @Synchronized
    fun edit(degree: Degree): Boolean {
        if (storage.values.map { it.id }.contains(degree.id)) {
            storage[degree.id] = degree
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

    fun findDegree(degree: Degree): Int? {
        storage.values.forEach { if (it == degree) return it.id }
        return null
    }
}
