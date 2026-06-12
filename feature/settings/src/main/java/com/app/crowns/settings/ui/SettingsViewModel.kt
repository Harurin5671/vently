package com.app.crowns.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.auth.usecase.CheckSubscriptionUseCase
import com.app.crowns.domain.auth.usecase.GetCurrentUserUseCase
import com.app.crowns.common.Result
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.company.usecase.GetDocumentSeriesUseCase
import com.app.crowns.domain.company.usecase.UpdateCertificatePasswordUseCase
import com.app.crowns.domain.company.usecase.UpdateCertificateUseCase
import com.app.crowns.domain.company.usecase.UpdateDocumentSeriesBatchUseCase
import com.app.crowns.domain.company.usecase.UpdateSolCredentialsUseCase
import com.app.crowns.settings.domain.usecase.DeleteAccountUseCase
import com.app.crowns.settings.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import com.app.crowns.common.util.toUserFriendlyMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val checkSubscriptionUseCase: CheckSubscriptionUseCase,
    private val updateCertificateUseCase: UpdateCertificateUseCase,
    private val updateCertificatePasswordUseCase: UpdateCertificatePasswordUseCase,
    private val getDocumentSeriesUseCase: GetDocumentSeriesUseCase,
    private val updateDocumentSeriesBatchUseCase: UpdateDocumentSeriesBatchUseCase,
    private val updateSolCredentialsUseCase: UpdateSolCredentialsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        loadCompanyInfo()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.Refresh -> loadCompanyInfo()
            is SettingsEvent.SilentRefresh -> loadCompanyInfo(isSilent = true)
            is SettingsEvent.Logout -> logout()
            is SettingsEvent.DeleteAccount -> deleteAccount()
            is SettingsEvent.UploadCertificate -> uploadCertificate(event.base64)
            is SettingsEvent.UpdateCertificatePassword -> updateCertificatePassword(event.password)
            is SettingsEvent.UpdateDocumentSeries -> updateDocumentSeries(
                event.boletaSerie,
                event.boletaCorrelative,
                event.facturaSerie,
                event.facturaCorrelative
            )
            is SettingsEvent.UpdateSolCredentials -> updateSolCredentials(event.username, event.password)
            is SettingsEvent.SetError -> _state.update { it.copy(error = event.message) }
            is SettingsEvent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            logoutUseCase().onSuccess {
                _state.update { it.copy(navigateToAuth = true, isLoading = false) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.toUserFriendlyMessage("Error al cerrar sesión"), isLoading = false) }
            }
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            deleteAccountUseCase().onSuccess {
                _state.update { it.copy(navigateToAuth = true, isDeleting = false) }
            }.onFailure { e ->
                _state.update { it.copy(error = e.toUserFriendlyMessage("Error al eliminar la cuenta"), isDeleting = false) }
            }
        }
    }

    private fun updateDocumentSeries(
        boletaSerie: String,
        boletaCorrelative: Int,
        facturaSerie: String,
        facturaCorrelative: Int
    ) {
        viewModelScope.launch {
            val currentState = _state.value
            val companyId = currentState.companyInfo?.id ?: return@launch

            val currentBoleta = currentState.documentSeries
                .find { it.documentType == ElectronicDocumentTypeEntity.BOLETA }
            val currentFactura = currentState.documentSeries
                .find { it.documentType == ElectronicDocumentTypeEntity.FACTURA }

            val boletaHasChanged = currentBoleta?.lastCorrelative != boletaCorrelative || currentBoleta?.serie != boletaSerie
            val facturaHasChanged = currentFactura?.lastCorrelative != facturaCorrelative || currentFactura?.serie != facturaSerie

            if (!boletaHasChanged && !facturaHasChanged) return@launch

            _state.update { it.copy(isLoading = true) }

            val seriesToUpdate = buildList {
                if (boletaHasChanged) add(
                    currentBoleta?.copy(serie = boletaSerie, lastCorrelative = boletaCorrelative)
                        ?: DocumentSeries(
                            companyId = companyId,
                            documentType = ElectronicDocumentTypeEntity.BOLETA,
                            serie = boletaSerie,
                            lastCorrelative = boletaCorrelative
                        )
                )
                if (facturaHasChanged) add(
                    currentFactura?.copy(serie = facturaSerie, lastCorrelative = facturaCorrelative)
                        ?: DocumentSeries(
                            companyId = companyId,
                            documentType = ElectronicDocumentTypeEntity.FACTURA,
                            serie = facturaSerie,
                            lastCorrelative = facturaCorrelative
                        )
                )
            }

            updateDocumentSeriesBatchUseCase(seriesToUpdate).fold(
                onSuccess = { loadCompanyInfo() },
                onFailure = { _state.update { it.copy(error = "Error al actualizar las series", isLoading = false) } }
            )
        }
    }

    private fun updateSolCredentials(username: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateSolCredentialsUseCase(username, password).onSuccess {
                loadCompanyInfo()
            }.onFailure { e ->
                _state.update { it.copy(error = e.toUserFriendlyMessage("Error al actualizar credenciales SOL"), isLoading = false) }
            }
        }
    }

    private fun updateCertificatePassword(password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateCertificatePasswordUseCase(password).onSuccess {
                loadCompanyInfo()
            }.onFailure { e ->
                _state.update { it.copy(error = e.toUserFriendlyMessage("Error al actualizar contraseña"), isLoading = false) }
            }
        }
    }

    private fun uploadCertificate(base64: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateCertificateUseCase(base64).onSuccess {
                loadCompanyInfo()
            }.onFailure { e ->
                val message = if (e.message?.contains("INVALID_CERTIFICATE_FORMAT") == true)
                    "El certificado no es válido. Sube un archivo .p12 o .pfx"
                else
                    e.toUserFriendlyMessage("Error al subir certificado")
                _state.update { it.copy(error = message, isLoading = false) }
            }
        }
    }

    private fun loadCompanyInfo(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent) _state.update { it.copy(isLoading = true) }

            val user = getCurrentUserUseCase()
            val isSubscriptionActive = checkSubscriptionUseCase() is Result.Success

            getCompanyUseCase().onSuccess { company ->
                if (company != null) {
                    val seriesResult = getDocumentSeriesUseCase(company.id)
                    _state.update {
                        it.copy(
                            companyInfo = company,
                            userInfo = user,
                            documentSeries = seriesResult.getOrDefault(emptyList()),
                            isSubscriptionActive = isSubscriptionActive,
                            isLoading = false
                        )
                    }
                } else {
                    _state.update { it.copy(companyInfo = null, userInfo = user, isSubscriptionActive = isSubscriptionActive, isLoading = false) }
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.toUserFriendlyMessage("Error al cargar información"), userInfo = user, isSubscriptionActive = isSubscriptionActive, isLoading = false) }
            }
        }
    }
}
