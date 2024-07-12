package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel
import ru.yarsu.domain.entities.Degree

class NewSpecialistVM(
    val degrees: List<Degree>,
    val form: WebForm?,
) : ViewModel
