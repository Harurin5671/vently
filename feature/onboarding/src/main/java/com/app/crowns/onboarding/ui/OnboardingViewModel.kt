package com.app.crowns.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<OnboardingNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.NextPage -> nextPage()
            is OnboardingEvent.PreviousPage -> previousPage()
            is OnboardingEvent.Finish -> finish()
            is OnboardingEvent.OnChangedRuc -> onChangedRuc(ruc = event.ruc)
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

    private fun finish() {
        viewModelScope.launch {
            _navigationEvent.send(OnboardingNavigationEvent.NavigateToHome)
        }
    }

    private fun onChangedRuc(ruc: String) {
        _state.update { it.copy(ruc = ruc, isValid = ruc.length == 11) }
    }
}