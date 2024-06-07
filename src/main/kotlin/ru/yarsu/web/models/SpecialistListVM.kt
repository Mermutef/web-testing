package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.entities.SpecialistsOnPage

class SpecialistListVM(
    val concentrator: SpecialistsOnPage,
    val minRegisterDate: String?,
    val maxRegisterDate: String?,
    val dateIsCorrect: Boolean,
) : ViewModel
