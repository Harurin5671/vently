package com.app.crowns.home.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val companyInfo: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val selectedTab: Int = 0,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCompanyUseCase: GetCompanyUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState(selectedTab = savedStateHandle["initialTab"] ?: 0))
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadCompanyInfo()
    }

    fun onTabSelected(index: Int) {
        _state.update { it.copy(selectedTab = index) }
    }

    private fun loadCompanyInfo() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = getCompanyUseCase()
            _state.update { 
                it.copy(
                    companyInfo = result.getOrNull(),
                    isLoading = false
                )
            }
        }
    }
}
