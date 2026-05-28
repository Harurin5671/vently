package com.app.crowns.settings.ui

sealed interface SettingsEvent {
    data class UploadCertificate(val base64: String) : SettingsEvent
    data class UpdateCertificatePassword(val password: String) : SettingsEvent
}
