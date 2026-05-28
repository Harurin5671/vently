package com.app.crowns.settings.ui

sealed interface SettingsEvent {
    data class UploadCertificate(val base64: String) : SettingsEvent
}
