package com.app.crowns.settings.ui

import com.app.crowns.domain.auth.model.User
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.model.DocumentSeries

data class SettingsUiState(
    val companyInfo: CompanyInfo? = null,
    val userInfo: User? = null,
    val documentSeries: List<DocumentSeries> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val error: String? = null,
    val navigateToAuth: Boolean = false,
)
