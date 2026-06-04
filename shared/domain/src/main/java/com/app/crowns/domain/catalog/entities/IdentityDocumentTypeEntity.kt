package com.app.crowns.domain.catalog.entities

import android.health.connect.datatypes.units.Length
import kotlinx.serialization.Serializable

@Serializable
data class IdentityDocumentTypeEntity(
    val code: String,
    val name: String,
    val maxLength: Long? = null,
    val isForPerson: Boolean = true,
    val isForCompany: Boolean = false,
) {
    enum class Type(val code: String) {
        NONE("0"),
        DNI("1"),
        CE("4"),
        RUC("6"),
        PASSPORT("7"),
        CEDULA_DIPLOMATICA("A"),
        DOC_IDENTIDAD_PAIS_RESIDENCIA("B"),
        UNKNOWN("");

        companion object {
            fun from(code: String) = entries.find { it.code == code } ?: UNKNOWN
        }
    }

    val type: Type get() = Type.from(code)

    val isNone: Boolean get() = type == Type.NONE
    val isDni: Boolean get() = type == Type.DNI
    val isRuc: Boolean get() = type == Type.RUC

    /**
     * Valida si el número de documento y el nombre son correctos para este tipo.
     */
    fun validate(number: String, name: String, businessName: String): Boolean {
        val limit = maxLength?.toInt() ?: 15
        return when (type) {
            Type.NONE -> true
            Type.RUC -> number.length == 11 && businessName.isNotBlank()
            Type.DNI -> number.length == 8 && name.isNotBlank()
            Type.CE -> number.length <= limit && number.isNotBlank() && name.isNotBlank()
            Type.PASSPORT -> number.length <= limit && number.isNotBlank() && name.isNotBlank()
            Type.CEDULA_DIPLOMATICA -> number.length <= limit && number.isNotBlank() && name.isNotBlank()
            Type.DOC_IDENTIDAD_PAIS_RESIDENCIA -> number.length <= limit && number.isNotBlank() && name.isNotBlank()
            Type.UNKNOWN -> number.isNotBlank() && (name.isNotBlank() || businessName.isNotBlank())
        }
    }
}
