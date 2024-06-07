package ru.yarsu.domain.entities

import org.http4k.core.Uri
import org.http4k.core.query
import org.http4k.core.removeQuery
import kotlin.math.ceil
import kotlin.math.min

class Paginator(
    private val uri: Uri,
    private val currentPage: Int,
    private val pagesAmount: Int,
) {
    companion object {
        fun elementsByPageNumber(
            elementsCount: Int,
            pageNumber: Int,
            elementsOnPage: Int = 15,
        ): Map<String, Int> {
            val start = elementsOnPage * pageNumber
            val end = min(elementsCount - 1, elementsOnPage * (pageNumber + 1) - 1)
            return mapOf("start" to start, "end" to end)
        }

        fun pagesAmount(
            pageCount: Int,
            elementsOnPage: Int = 15,
        ): Int {
            return ceil(1.0 * pageCount / elementsOnPage).toInt()
        }
    }

    fun getPrevious(): Uri {
        return uri.removeQuery("page").query("page", "${currentPage - 1}")
    }

    fun getNext(): Uri {
        return uri.removeQuery("page").query("page", "${currentPage + 1}")
    }

    fun hasNextPage(): Boolean {
        return currentPage < pagesAmount - 1
    }

    fun hasPreviousPage(): Boolean {
        return currentPage > 0
    }

    fun currentPage(): Int {
        return currentPage + 1
    }
}
