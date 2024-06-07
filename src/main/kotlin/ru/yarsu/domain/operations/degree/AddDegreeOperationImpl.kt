package ru.yarsu.domain.operations.degree

import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.storages.DegreeStorage

interface AddDegreeOperation {
    fun add(degree: Degree): Int
}

class AddDegreeOperationImpl(
    private val storage: DegreeStorage,
) : AddDegreeOperation {
    override fun add(degree: Degree): Int = storage.add(degree)
}
