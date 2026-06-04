package com.app.crowns.onboarding.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.network.NetworkResult
import com.app.crowns.onboarding.domain.usecase.GetCompanyInfoUseCase
import com.app.crowns.onboarding.domain.usecase.SaveCompanyUseCase
import com.app.crowns.onboarding.domain.usecase.UploadCompanyLogoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import com.app.crowns.common.util.toUserFriendlyMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getCompanyInfoUseCase: GetCompanyInfoUseCase,
    private val saveCompanyUseCase: SaveCompanyUseCase,
    private val uploadCompanyLogoUseCase: UploadCompanyLogoUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<OnboardingNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.NextPage -> nextPage()
            is OnboardingEvent.PreviousPage -> previousPage()
            is OnboardingEvent.Finish -> saveAndFinish(event.logoBytes)
            is OnboardingEvent.OnChangedRuc -> onChangedRuc(ruc = event.ruc)
            is OnboardingEvent.OnLogoSelected -> _state.update { it.copy(logoUri = event.uri) }
        }
    }

    private fun nextPage() {
        val current = _state.value
        if (!current.isLastPage) {
            _state.update { it.copy(currentPage = it.currentPage + 1) }
        }
    }

    private fun previousPage() {
        val current = _state.value
        if (!current.isFirstPage) {
            _state.update { it.copy(currentPage = it.currentPage - 1) }
        }
    }

    private fun saveAndFinish(logoBytes: ByteArray?) {
        viewModelScope.launch {
            val state = _state.value
            if (state.isFinishing) return@launch
            val companyInfo = state.companyInfo ?: return@launch
            _state.update { it.copy(isFinishing = true, searchError = null) }

            var logoUrl: String? = null
            if (logoBytes != null) {
                uploadCompanyLogoUseCase(
                    imageBytes = logoBytes,
                ).onSuccess { url ->
                    logoUrl = url
                }
            }

            saveCompanyUseCase(companyInfo, logoUrl)
                .onSuccess {
                    _navigationEvent.send(OnboardingNavigationEvent.NavigateToHome)
                }
                .onFailure { error ->
                    val message = error.toUserFriendlyMessage("Error al guardar los datos")
                    _state.update {
                        it.copy(
                            isFinishing = false,
                            searchError = message
                        )
                    }
                }
        }
    }

    private fun onChangedRuc(ruc: String) {
        _state.update {
            it.copy(
                ruc = ruc,
                isValid = ruc.length == 11,
                companyInfo = null,
                searchError = null
            )
        }

        if (ruc.length == 11) searchCompany(ruc)
    }

    private fun searchCompany(ruc: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSearching = true, searchError = null) }
            val result = getCompanyInfoUseCase(ruc)
            Log.d("OnboardingViewModel", "Search result for RUC $ruc: $result")
            when (result) {
                is NetworkResult.Success -> _state.update {
                    it.copy(
                        isSearching = false,
                        isValid = true,
                        companyInfo = result.data,
                    )
                }
                is NetworkResult.HttpError -> _state.update {
                    it.copy(
                        isSearching = false,
                        isValid = false,
                        searchError = "Error ${result.code}: ${result.message}",
                    )
                }
                is NetworkResult.NetworkError -> _state.update {
                    it.copy(
                        isSearching = false,
                        isValid = false,
                        searchError = "Sin conexión a internet",
                    )
                }
                is NetworkResult.Unauthorized -> _state.update {
                    it.copy(
                        isSearching = false,
                        isValid = false,
                        searchError = "Sesión no autorizada",
                    )
                }
            }
        }
    }
}