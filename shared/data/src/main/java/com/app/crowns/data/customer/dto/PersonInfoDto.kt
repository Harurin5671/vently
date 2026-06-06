package com.app.crowns.data.customer.dto

import com.app.crowns.domain.customer.model.PersonInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonInfoDto(
    @SerialName("first_name") val firstName: String,
    @SerialName("first_last_name") val firstLastName: String,
    @SerialName("second_last_name") val secondLastName: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("document_number") val documentNumber: String,
)

fun PersonInfoDto.toDomain(): PersonInfo {
    return PersonInfo(
        firstName = firstName,
        firstLastName = firstLastName,
        secondLastName = secondLastName,
        fullName = fullName,
        documentNumber = documentNumber
    )
}
