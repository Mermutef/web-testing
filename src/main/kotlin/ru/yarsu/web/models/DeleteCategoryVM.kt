package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

class DeleteCategoryVM(
    val categoryId: Int,
    val form: WebForm?,
) : ViewModel
