package com.app.crowns.home.ui

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
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCompanyUseCase: GetCompanyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadCompanyInfo()
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
