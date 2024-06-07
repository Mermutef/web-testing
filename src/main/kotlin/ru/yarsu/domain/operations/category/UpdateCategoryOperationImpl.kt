package ru.yarsu.domain.operations.category

import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.storages.CategoryStorage

interface UpdateCategoryOperation {
    fun update(category: Category): Int
}

class UpdateCategoryOperationImpl(private val categoryStorage: CategoryStorage) :
    UpdateCategoryOperation {
    override fun update(category: Category): Int {
        if (category.id != -1) {
            categoryStorage.edit(category)
            return category.id
        }
        return categoryStorage.add(category)
    }
}
