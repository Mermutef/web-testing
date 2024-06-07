package ru.yarsu.domain.operations.category

import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.storages.CategoryStorage

interface GetLexiSortedCategoriesOperation {
    fun getLexiSortedCategories(reverse: Boolean = false): List<Category>
}

class GetLexiSortedCategoriesOperationImpl(
    private val storage: CategoryStorage,
) :
    GetLexiSortedCategoriesOperation {
    override fun getLexiSortedCategories(reverse: Boolean): List<Category> {
        if (reverse) {
            return storage.getAll().sortedByDescending { it.ru }
        }
        return storage.getAll().sortedBy { it.ru }
    }
}
