package com.app.crowns.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.company.usecase.UpdateCertificateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val companyInfo: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCompanyUseCase: GetCompanyUseCase,
    private val updateCertificateUseCase: UpdateCertificateUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        loadCompanyInfo()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UploadCertificate -> uploadCertificate(event.base64)
        }
    }

    private fun uploadCertificate(base64: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateCertificateUseCase(base64).onSuccess {
                loadCompanyInfo()
            }.onFailure { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun loadCompanyInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getCompanyUseCase().onSuccess { company ->
                _state.update { it.copy(companyInfo = company, isLoading = false) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
}
