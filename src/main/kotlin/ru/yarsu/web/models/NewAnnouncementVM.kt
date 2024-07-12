package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

class NewAnnouncementVM(
    val form: WebForm?,
) : ViewModel
