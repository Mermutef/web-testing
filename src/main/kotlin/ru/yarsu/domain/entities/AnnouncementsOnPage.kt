package ru.yarsu.domain.entities

data class AnnouncementsOnPage(
    val announcements: List<Announcement>,
    val paginator: Paginator,
)
