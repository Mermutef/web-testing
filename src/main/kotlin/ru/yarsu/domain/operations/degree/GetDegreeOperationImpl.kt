package ru.yarsu.domain.operations.degree

import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.storages.DegreeStorage

interface GetDegreeOperation {
    fun get(id: Int): Degree?
}

class GetDegreeOperationImpl(
    private val storage: DegreeStorage,
) : GetDegreeOperation {
    override fun get(id: Int): Degree? {
        return storage.get(id)
    }
}
