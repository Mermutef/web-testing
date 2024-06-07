package ru.yarsu.domain.entities

data class SpecialistsOnPage(
    val specialists: List<Specialist>,
    val paginator: Paginator,
)
