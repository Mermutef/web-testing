package ru.yarsu.domain.storages

import ru.yarsu.domain.entities.Announcement

class AnnouncementStorage(
    announcements: List<Announcement>,
) {
    private val storage: MutableMap<Int, Announcement>
    private var firstFree: Int

    init {
        if (announcements.isEmpty()) {
            storage = mutableMapOf()
            firstFree = 0
        } else {
            require(announcements.distinctBy { it.id }.size == announcements.size) {
                "Обнаружено дублирование id в базе объявлений. Исправьте данные и повторите попытку."
            }
            storage = announcements.associateBy({ it.id }, { it }).toMutableMap()
            firstFree = storage.values.maxOf { it.id } + 1
        }
    }

    fun get(id: Int): Announcement? = storage[id]

    fun getAll(): List<Announcement> = storage.values.toList()

    fun add(announcement: Announcement): Int {
        storage[firstFree] = announcement.copy(id = firstFree)
        ++firstFree
        return firstFree - 1
    }

    @Synchronized
    fun edit(announcement: Announcement): Boolean {
        if (storage.values.map { it.id }.contains(announcement.id)) {
            storage[announcement.id] = announcement
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
