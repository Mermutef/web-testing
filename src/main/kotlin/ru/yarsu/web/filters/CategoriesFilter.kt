package ru.yarsu.web.filters

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.storages.StoragesOperationsAndMethods

class CategoriesFilter(
    private val addCategoriesToContextLens: RequestContextLens<List<Category>>,
    private val storagesOperations: StoragesOperationsAndMethods,
) : Filter {
    override fun invoke(next: HttpHandler): HttpHandler =
        {
            next(
                it.with(
                    addCategoriesToContextLens of
                        storagesOperations.getLexiSortedCategories.getLexiSortedCategories(),
                ),
            )
        }
}

/**
 * 0. Контекст
 * 1. Линза
 * 2. Фильтр: по линзе добавляет в контекст
 * 3. В модели достаешь по ключу
 */
