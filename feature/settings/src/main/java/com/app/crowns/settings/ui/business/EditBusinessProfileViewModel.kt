package com.app.crowns.settings.ui.business

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditBusinessProfileUiState(
    val companyInfo: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    // Editable fields
    val tradeName: String = "",
    val address: String = "",
    val logoUrl: String? = null,
    val selectedLogoBytes: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EditBusinessProfileUiState

        if (companyInfo != other.companyInfo) return false
        if (isLoading != other.isLoading) return false
        if (isSaving != other.isSaving) return false
        if (error != other.error) return false
        if (isSaved != other.isSaved) return false
        if (tradeName != other.tradeName) return false
        if (address != other.address) return false
        if (logoUrl != other.logoUrl) return false
        if (selectedLogoBytes != null) {
            if (other.selectedLogoBytes == null) return false
            if (!selectedLogoBytes.contentEquals(other.selectedLogoBytes)) return false
        } else if (other.selectedLogoBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = companyInfo?.hashCode() ?: 0
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + isSaving.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + isSaved.hashCode()
        result = 31 * result + tradeName.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + (logoUrl?.hashCode() ?: 0)
        result = 31 * result + (selectedLogoBytes?.contentHashCode() ?: 0)
        return result
    }
}

@HiltViewModel
class EditBusinessProfileViewModel @Inject constructor(
    private val repository: CompanyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditBusinessProfileUiState())
    val state: StateFlow<EditBusinessProfileUiState> = _state.asStateFlow()

    init {
        loadCompany()
    }

    private fun loadCompany() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getCompany()
                .onSuccess { company ->
                    _state.update {
                        it.copy(
                            companyInfo = company,
                            isLoading = false,
                            tradeName = company?.tradeName ?: "",
                            address = company?.address ?: "",
                            logoUrl = company?.logoUrl
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }

    fun onTradeNameChange(newName: String) {
        _state.update { it.copy(tradeName = newName) }
    }

    fun onAddressChange(newAddress: String) {
        _state.update { it.copy(address = newAddress) }
    }

    fun onLogoSelected(bytes: ByteArray) {
        _state.update { it.copy(selectedLogoBytes = bytes) }
    }

    fun onSave() {
        val currentCompany = _state.value.companyInfo ?: return
        Log.d("EDIT_PROFILE_VM", "Iniciando proceso de guardado...")
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            
            var finalLogoUrl = _state.value.logoUrl
            
            // 1. Subir logo si hay uno nuevo seleccionado
            _state.value.selectedLogoBytes?.let { bytes ->
                Log.d("EDIT_PROFILE_VM", "Subiendo nuevo logo...")
                repository.uploadLogo(bytes)
                    .onSuccess { url ->
                        Log.d("EDIT_PROFILE_VM", "Logo subido exitosamente: $url")
                        finalLogoUrl = url
                    }
                    .onFailure { error ->
                        Log.e("EDIT_PROFILE_VM", "Error al subir logo", error)
                        _state.update { it.copy(isSaving = false, error = "Error al subir logo: ${error.message}") }
                        return@launch
                    }
            }

            // 2. Actualizar datos de la compañía
            Log.d("EDIT_PROFILE_VM", "Actualizando datos de la compañía...")
            val updatedCompany = currentCompany.copy(
                tradeName = _state.value.tradeName,
                address = _state.value.address,
                logoUrl = finalLogoUrl
            )
            repository.updateCompany(updatedCompany)
                .onSuccess {
                    Log.d("EDIT_PROFILE_VM", "Compañía actualizada exitosamente")
                    _state.update { it.copy(isSaving = false, isSaved = true) }
                }
                .onFailure { error ->
                    Log.e("EDIT_PROFILE_VM", "Error al actualizar compañía", error)
                    _state.update { it.copy(isSaving = false, error = error.message) }
                }
        }
    }

    fun resetSavedState() {
        _state.update { it.copy(isSaved = false) }
    }
}
