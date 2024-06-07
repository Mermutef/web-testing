package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.entities.AnnouncementsOnPage
import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.entities.Specialist

class AnnouncementListVM(
    val concentrator: AnnouncementsOnPage,
    val pageCategory: Category,
    val minAnnouncementDate: String?,
    val maxAnnouncementDate: String?,
    val dateIsCorrect: Boolean,
    val specialists: Map<Int, Specialist?>,
) : ViewModel
