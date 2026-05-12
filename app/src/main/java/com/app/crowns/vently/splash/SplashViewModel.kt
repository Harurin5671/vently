package com.app.crowns.vently.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.auth.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isSessionActive = MutableStateFlow<Boolean?>(null)
    val isSessionActive = _isSessionActive.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val isActive = authRepository.isSessionActive()
            delay(1500L) // Ensure splash is visible for a moment
            _isSessionActive.value = isActive
        }
    }
}
