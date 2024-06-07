package ru.yarsu.domain.operations.category

import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.storages.CategoryStorage

interface GetCategoryOperation {
    fun get(id: Int): Category?
}

class GetCategoryOperationImpl(
    private val storage: CategoryStorage,
) : GetCategoryOperation {
    override fun get(id: Int): Category? {
        return storage.get(id)
    }
}
