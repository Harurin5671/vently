package com.app.crowns.vently.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.company.usecase.HasCompanyUseCase
import com.app.crowns.domain.auth.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val hasCompanyUseCase: HasCompanyUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val splashTimer = async { delay(1500L) }
            val isAuthenticated = authRepository.observeSession().first()
            splashTimer.await()

            if (isAuthenticated) {
                if (hasCompanyUseCase()) {
                    _uiState.value = SplashUiState.NavigateToHome
                } else {
                    _uiState.value = SplashUiState.NavigateToOnboarding
                }
            } else {
                _uiState.value = SplashUiState.NavigateToLogin
            }
        }
    }
}

sealed class SplashUiState {
    object Idle : SplashUiState()
    object NavigateToLogin : SplashUiState()
    object NavigateToOnboarding : SplashUiState()
    object NavigateToHome : SplashUiState()
}
