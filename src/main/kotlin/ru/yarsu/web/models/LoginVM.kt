package ru.yarsu.web.models

import org.http4k.lens.WebForm
import org.http4k.template.ViewModel

class LoginVM(val form: WebForm?, val invalidLoginOrPassword: String? = null) : ViewModel
