package com.app.crowns.settings.ui

sealed interface SettingsEvent {
    data object Refresh : SettingsEvent
    data object Logout : SettingsEvent
    data object DeleteAccount : SettingsEvent
    data class UploadCertificate(val base64: String) : SettingsEvent
    data class UpdateCertificatePassword(val password: String) : SettingsEvent
    data class UpdateDocumentSeries(
        val boletaSerie: String,
        val boletaCorrelative: Int,
        val facturaSerie: String,
        val facturaCorrelative: Int
    ) : SettingsEvent
    data class UpdateSolCredentials(val username: String, val password: String) : SettingsEvent
}
