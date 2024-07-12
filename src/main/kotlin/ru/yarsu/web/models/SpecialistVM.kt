package ru.yarsu.web.models

import org.http4k.template.ViewModel
import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.entities.Specialist

class SpecialistVM(
    val specialist: Specialist,
    val degrees: List<Degree>,
) : ViewModel
