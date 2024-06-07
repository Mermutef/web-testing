package ru.yarsu.domain.entities

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class Specialist(
    val id: Int,
    val fcs: String,
    val degree: List<Int>,
    val phone: String,
    val vkId: String,
    val login: String,
    val password: String,
    @JsonProperty("registerDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(
        using = LocalDateTimeDeserializer::class,
    )
    val registerDate: LocalDateTime,
    val permissions: Int,
) : Comparable<Specialist> {
    init {
        require(EntitiesCheckAndHelpMethods.checkFCS(fcs)) {
            "ФИО специалиста не может быть пустой строкой"
        }
        require(EntitiesCheckAndHelpMethods.checkPhone(phone)) {
            "Телефон может содержать только цифры"
        }
        require(EntitiesCheckAndHelpMethods.checkSocialId(vkId)) {
            "Индетификатор социальной сети может состоять только из латинских букв, цифр и знака '-'"
        }
        require(EntitiesCheckAndHelpMethods.checkLogin(login)) {
            "Логин может состоять только из латинских букв и цифр, иметь длину от 2 до 20 символов и начинаться с буквы"
        }
    }

    override fun compareTo(other: Specialist): Int = this.login.compareTo(other.login)
}
