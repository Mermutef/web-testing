package ru.yarsu.domain.operations.specialist

import org.http4k.core.Uri
import org.http4k.core.query
import org.http4k.core.removeQuery
import ru.yarsu.domain.entities.Paginator
import ru.yarsu.domain.entities.SpecialistsOnPage
import ru.yarsu.domain.storages.SpecialistStorage
import java.time.LocalDateTime

interface DateTimeFilterOperation {
    fun dateTimeFilter(
        page: Int,
        minRegisterDate: LocalDateTime?,
        maxRegisterDate: LocalDateTime?,
        uri: Uri,
    ): SpecialistsOnPage
}

class SpecialistDateTimeFilterOperationImpl(private val storage: SpecialistStorage) : DateTimeFilterOperation {
    override fun dateTimeFilter(
        page: Int,
        minRegisterDate: LocalDateTime?,
        maxRegisterDate: LocalDateTime?,
        uri: Uri,
    ): SpecialistsOnPage {
        var specialists = storage.getAll().sortedBy { it.registerDate }
        if (minRegisterDate != null) {
            specialists = specialists.filter { it.registerDate >= minRegisterDate }
        }
        if (maxRegisterDate != null) {
            specialists = specialists.filter { it.registerDate >= maxRegisterDate }
        }
        val pagesAmount = Paginator.pagesAmount(specialists.size)
        specialists =
            if (page in 0..<pagesAmount) {
                val sliceIndices = Paginator.elementsByPageNumber(specialists.size, page)
                specialists.slice(
                    sliceIndices["end"]
                        ?.let {
                            sliceIndices["start"]
                                ?.rangeTo(it)
                        }
                        ?: 0..specialists.size,
                )
            } else {
                listOf()
            }
        return SpecialistsOnPage(
            specialists,
            Paginator(
                if (page == 0) {
                    uri.removeQuery("page").query("page", "1")
                } else {
                    uri
                },
                page,
                pagesAmount,
            ),
        )
    }
}
