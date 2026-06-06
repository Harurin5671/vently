package com.app.crowns.settings.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.auth.usecase.GetCurrentUserUseCase
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.company.usecase.GetDocumentSeriesUseCase
import com.app.crowns.domain.company.usecase.UpdateCertificatePasswordUseCase
import com.app.crowns.domain.company.usecase.UpdateCertificateUseCase
import com.app.crowns.domain.company.usecase.UpdateDocumentSeriesUseCase
import com.app.crowns.domain.company.usecase.UpdateSolCredentialsUseCase
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
    private val updateCertificateUseCase: UpdateCertificateUseCase,
    private val updateCertificatePasswordUseCase: UpdateCertificatePasswordUseCase,
    private val getDocumentSeriesUseCase: GetDocumentSeriesUseCase,
    private val updateDocumentSeriesUseCase: UpdateDocumentSeriesUseCase,
    private val updateSolCredentialsUseCase: UpdateSolCredentialsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        loadCompanyInfo()
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.Refresh -> loadCompanyInfo()
            is SettingsEvent.UploadCertificate -> uploadCertificate(event.base64)
            is SettingsEvent.UpdateCertificatePassword -> updateCertificatePassword(event.password)
            is SettingsEvent.UpdateDocumentSeries -> updateDocumentSeries(
                event.boletaSerie,
                event.boletaCorrelative,
                event.facturaSerie,
                event.facturaCorrelative
            )
            is SettingsEvent.UpdateSolCredentials -> updateSolCredentials(event.username, event.password)
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

            var success = true
            
            if (boletaHasChanged) {
                val boleta = currentBoleta?.copy(serie = boletaSerie, lastCorrelative = boletaCorrelative)
                    ?: DocumentSeries(
                        companyId = companyId,
                        documentType = ElectronicDocumentTypeEntity.BOLETA,
                        serie = boletaSerie,
                        lastCorrelative = boletaCorrelative
                    )
                if (updateDocumentSeriesUseCase(boleta).isFailure) success = false
            }

            if (facturaHasChanged) {
                val factura = currentFactura?.copy(serie = facturaSerie, lastCorrelative = facturaCorrelative)
                    ?: DocumentSeries(
                        companyId = companyId,
                        documentType = ElectronicDocumentTypeEntity.FACTURA,
                        serie = facturaSerie,
                        lastCorrelative = facturaCorrelative
                    )
                if (updateDocumentSeriesUseCase(factura).isFailure) success = false
            }

            if (success) {
                loadCompanyInfo()
            } else {
                _state.update { it.copy(error = "Error al actualizar las series", isLoading = false) }
            }
        }
    }

    private fun updateSolCredentials(username: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateSolCredentialsUseCase(username, password).onSuccess {
                loadCompanyInfo()
            }.onFailure { e ->
                Log.e("SETTINGS_DEBUG", "Error updating SOL credentials: ${e.javaClass.simpleName}: ${e.message}", e)
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
                Log.e("SETTINGS_DEBUG", "Error updating certificate password: ${e.javaClass.simpleName}: ${e.message}", e)
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
                Log.e("SETTINGS_DEBUG", "Error uploading certificate: ${e.javaClass.simpleName}: ${e.message}", e)
                _state.update { it.copy(error = e.toUserFriendlyMessage("Error al subir certificado"), isLoading = false) }
            }
        }
    }

    private fun loadCompanyInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val user = getCurrentUserUseCase()
            
            getCompanyUseCase().onSuccess { company ->
                if (company != null) {
                    val seriesResult = getDocumentSeriesUseCase(company.id)
                    _state.update { 
                        it.copy(
                            companyInfo = company,
                            userInfo = user,
                            documentSeries = seriesResult.getOrDefault(emptyList()),
                            isLoading = false 
                        ) 
                    }
                } else {
                    _state.update { it.copy(companyInfo = null, userInfo = user, isLoading = false) }
                }
            }.onFailure { e ->
                _state.update { it.copy(error = e.toUserFriendlyMessage("Error al cargar información"), userInfo = user, isLoading = false) }
            }
        }
    }
}
