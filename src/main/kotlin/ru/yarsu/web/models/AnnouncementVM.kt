package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.entities.Announcement
import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.entities.Specialist

class AnnouncementVM(
    val announcement: Announcement,
    val specialist: Specialist,
    val category: Category,
    val degrees: List<Degree>,
    val uriBack: String,
) : ViewModel
