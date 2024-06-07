package ru.yarsu.domain.entities

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime
import kotlin.math.min

const val MAX_DESCRIPTION_LENGTH = 200

data class Announcement(
    val id: Int,
    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(
        using = LocalDateTimeDeserializer::class,
    )
    val date: LocalDateTime,
    val category: Int,
    val title: String,
    val description: String,
    val specialist: Int,
) {
    fun trimDescription(): String =
        description.substring(
            0,
            min(MAX_DESCRIPTION_LENGTH, description.length),
        ) + if (description.length > MAX_DESCRIPTION_LENGTH) "..." else ""

    fun htmlDescription(): List<String> = description.split("\n")
}
