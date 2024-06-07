package ru.yarsu.domain.operations.degree

import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.storages.DegreeStorage

interface GetMainDegreesOperation {
    fun getMainDegrees(): List<Degree>
}

class GetMainDegreesOperationImpl(
    private val storage: DegreeStorage,
) :
    GetMainDegreesOperation {
    override fun getMainDegrees(): List<Degree> = storage.getAll().filter { it.type == "main" }
}
