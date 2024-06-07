package ru.yarsu.domain.operations.degree

import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.storages.DegreeStorage

interface EncodeDegreesOperation {
    fun encodeDegrees(specialist: Specialist): List<Degree>
}

class EncodeDegreesOperationImpl(
    private val storage: DegreeStorage,
) : EncodeDegreesOperation {
    override fun encodeDegrees(specialist: Specialist): List<Degree> =
        storage
            .getAll()
            .filter {
                specialist
                    .degree
                    .contains(it.id)
            }
}
