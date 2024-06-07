package ru.yarsu.domain.operations.category

import ru.yarsu.domain.storages.CategoryStorage

interface DeleteCategoryOperation {
    fun delete(id: Int): Boolean
}

class DeleteCategoryOperationImpl(
    private val storage: CategoryStorage,
) : DeleteCategoryOperation {
    override fun delete(id: Int): Boolean = storage.delete(id)
}
