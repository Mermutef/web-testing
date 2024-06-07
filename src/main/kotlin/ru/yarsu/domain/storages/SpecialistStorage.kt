package ru.yarsu.domain.storages

import ru.yarsu.domain.entities.Specialist

class SpecialistStorage(
    specialists: List<Specialist>,
) {
    private val storage: MutableMap<Int, Specialist>
    private var firstFree: Int

    init {
        if (specialists.isEmpty()) {
            storage = mutableMapOf()
            firstFree = 0
        } else {
            require(specialists.distinctBy { it.id }.size == specialists.size) {
                "Обнаружено дублирование id в базе специалистов. Исправьте данные и повторите попытку."
            }
            require(specialists.distinctBy { it.login }.size == specialists.size) {
                "Обнаружено дублирование имен пользователей в базе специалистов. Исправьте данные и повторите попытку."
            }
            storage = specialists.associateBy({ it.id }, { it }).toMutableMap()
            firstFree = storage.values.maxOf { it.id } + 1
        }
    }

    fun get(id: Int): Specialist? = storage[id]

    fun getAll(): List<Specialist> = storage.values.toList()

    fun getByLogin(login: String): Specialist? = storage.values.find { it.login == login }

    fun add(specialist: Specialist): Int {
        val existsSpecialist = findSpecialist(specialist)
        if (existsSpecialist != null) {
            return existsSpecialist
        }
        storage[firstFree] = specialist.copy(id = firstFree)
        ++firstFree
        return firstFree - 1
    }

    fun findSpecialist(specialist: Specialist): Int? {
        storage.values.forEach { if (it == specialist) return it.id }
        return null
    }

    @Synchronized
    fun edit(specialist: Specialist): Boolean {
        if (storage.values.map { it.id }.contains(specialist.id)) {
            storage[specialist.id] = specialist
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
