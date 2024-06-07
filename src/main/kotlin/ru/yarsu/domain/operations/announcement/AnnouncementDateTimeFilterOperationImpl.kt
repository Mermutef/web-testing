package ru.yarsu.domain.operations.announcement

import org.http4k.core.Uri
import org.http4k.core.query
import org.http4k.core.removeQuery
import ru.yarsu.domain.entities.Announcement
import ru.yarsu.domain.entities.AnnouncementsOnPage
import ru.yarsu.domain.entities.Paginator
import ru.yarsu.domain.storages.AnnouncementStorage
import java.time.LocalDateTime

interface DateTimeFilterOperation {
    fun dateTimeFilter(
        page: Int,
        minAnnouncementDate: LocalDateTime?,
        maxAnnouncementDate: LocalDateTime?,
        categoryId: Int,
        uri: Uri,
    ): AnnouncementsOnPage
}

class AnnouncementDateTimeFilterOperationImpl(private val storage: AnnouncementStorage) : DateTimeFilterOperation {
    private fun getByCategory(id: Int): List<Announcement> =
        storage
            .getAll()
            .filter {
                it.category == id
            }

    override fun dateTimeFilter(
        page: Int,
        minAnnouncementDate: LocalDateTime?,
        maxAnnouncementDate: LocalDateTime?,
        categoryId: Int,
        uri: Uri,
    ): AnnouncementsOnPage {
        var announcements = getByCategory(categoryId).sortedBy { it.date }
        if (minAnnouncementDate != null) {
            announcements = announcements.filter { it.date >= minAnnouncementDate }
        }
        if (maxAnnouncementDate != null) {
            announcements = announcements.filter { it.date <= maxAnnouncementDate }
        }
        val pagesAmount = Paginator.pagesAmount(announcements.size)
        announcements =
            if (page in 0..<pagesAmount) {
                val sliceIndices = Paginator.elementsByPageNumber(announcements.size, page)
                announcements.slice(
                    sliceIndices["end"]
                        ?.let {
                            sliceIndices["start"]
                                ?.rangeTo(it)
                        }
                        ?: 0..announcements.size,
                )
            } else {
                listOf()
            }
        return AnnouncementsOnPage(
            announcements,
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
