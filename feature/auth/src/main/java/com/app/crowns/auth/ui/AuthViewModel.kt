package com.app.crowns.auth.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.auth.domain.model.AuthError
import com.app.crowns.auth.domain.model.User
import com.app.crowns.common.Result
import com.app.crowns.auth.domain.usecase.GetCurrentUserUseCase
import com.app.crowns.auth.domain.usecase.IsSessionActiveUseCase
import com.app.crowns.auth.domain.usecase.LogoutUseCase
import com.app.crowns.domain.company.usecase.HasCompanyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val logout: LogoutUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    private val isSessionActive: IsSessionActiveUseCase,
    private val hasCompanyUseCase: HasCompanyUseCase
): ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<AuthNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        checkSession()
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.HandleGoogleResult -> handleGoogleResult(event.result)
            is AuthEvent.Logout -> performLogout()
            AuthEvent.SignInWithGoogle -> _state.update { it.copy(isLoading = true) }
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            val active = isSessionActive()
            if (active) {
                val user = getCurrentUser()
                _state.update { it.copy(user = user, isAuthenticated = true) }
                if (hasCompanyUseCase()) {
                    _navigationEvent.send(AuthNavigationEvent.NavigateToHome)
                } else {
                    _navigationEvent.send(AuthNavigationEvent.NavigateToOnboarding)
                }
            }
        }
    }

    private fun handleGoogleResult(result: NativeSignInResult) {
        Log.d("AuthViewModel", "Google result received: $result")
        viewModelScope.launch {
            when (result) {
                is NativeSignInResult.Success -> {
                    val user = getCurrentUser()
                    Log.d("AuthViewModel", "Login Success. User: $user")
                    _state.update {
                        it.copy(isLoading = false, isAuthenticated = true, user = user)
                    }
                    if (hasCompanyUseCase()) {
                        _navigationEvent.send(AuthNavigationEvent.NavigateToHome)
                    } else {
                        _navigationEvent.send(AuthNavigationEvent.NavigateToOnboarding)
                    }
                }

                is NativeSignInResult.Error -> {
                    Log.e("AuthViewModel", "Login Error: ${result.message}")
                    _state.update {
                        it.copy(isLoading = false, error = AuthError.Unknown(result.message))
                    }
                }

                is NativeSignInResult.NetworkError -> {
                    Log.e("AuthViewModel", "Network Error")
                    _state.update {
                        it.copy(isLoading = false, error = AuthError.NetworkError)
                    }
                }

                NativeSignInResult.ClosedByUser -> {
                    Log.d("AuthViewModel", "Closed by user")
                    _state.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun performLogout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = logout()) {
                is Result.Success<*> -> _state.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = false,
                        user = null
                    )
                }
                is Result.Failure<AuthError> -> _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.error
                    )
                }
            }
        }
    }
}