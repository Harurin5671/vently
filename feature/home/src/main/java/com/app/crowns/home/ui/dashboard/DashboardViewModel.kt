package com.app.crowns.home.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.catalog.usecase.GetInvoiceStatusesUseCase
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.invoice.usecase.CalculateDashboardStatsUseCase
import com.app.crowns.domain.invoice.usecase.GetInvoicesHistoryUseCase
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
class DashboardViewModel @Inject constructor(
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getInvoicesHistory: GetInvoicesHistoryUseCase,
    private val getInvoiceStatusesUseCase: GetInvoiceStatusesUseCase,
    private val calculateDashboardStats: CalculateDashboardStatsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<DashboardNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadDashboard()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.Refresh -> loadDashboard(isSilent = false)
            is DashboardEvent.OnInvoiceClick -> navigateToDetail(event.invoiceId)
            is DashboardEvent.OnNewBoleta -> checkCredentialsAndEmit(DashboardNavigationEvent.NavigateToNewBoleta)
            is DashboardEvent.OnNewFactura -> checkCredentialsAndEmit(DashboardNavigationEvent.NavigateToNewFactura)
            is DashboardEvent.OnStockClick -> emitNav(DashboardNavigationEvent.NavigateToStock)
            is DashboardEvent.OnHistoryClick -> emitNav(DashboardNavigationEvent.NavigateToHistory)
            is DashboardEvent.DismissMissingDataDialog -> _state.update { it.copy(showMissingDataDialog = false) }
            is DashboardEvent.OnGoToSettings -> {
                _state.update { it.copy(showMissingDataDialog = false) }
                emitNav(DashboardNavigationEvent.NavigateToSettings)
            }
            is DashboardEvent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun checkCredentialsAndEmit(navEvent: DashboardNavigationEvent) {
        val company = _state.value.companyInfo
        val isDataComplete = company?.certificateBase64 != null &&
                company.certificatePassword != null &&
                company.solUsername != null &&
                company.solPassword != null

        if (isDataComplete) {
            emitNav(navEvent)
        } else {
            _state.update { it.copy(showMissingDataDialog = true) }
        }
    }

    private fun loadDashboard(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent) _state.update { it.copy(isLoading = true) }

            val companyResult = getCompanyUseCase()
            val companyInfo = companyResult.getOrNull()

            val invoicesResult = getInvoicesHistory(java.time.YearMonth.now())
            if (invoicesResult.isFailure) {
                _state.update { it.copy(error = invoicesResult.exceptionOrNull()?.message ?: "Error al cargar datos") }
            }
            val rawInvoices = invoicesResult.getOrDefault(emptyList())

            val statusesResult = getInvoiceStatusesUseCase()
            val statuses = statusesResult.getOrDefault(emptyList())

            val invoices = rawInvoices.map { inv ->
                inv.copy(statusInfo = statuses.find { it.code == inv.status })
            }

            val stats = calculateDashboardStats(invoices)

            _state.update {
                it.copy(
                    isLoading = false,
                    userName = companyInfo?.businessName ?: "Usuario",
                    companyInfo = companyInfo,
                    todaySales = stats.todaySales,
                    salesGrowthPercent = stats.salesGrowthPercent,
                    totalInvoices = stats.totalInvoices,
                    acceptedInvoices = stats.acceptedInvoices,
                    pendingInvoices = stats.pendingInvoices,
                    recentInvoices = stats.recentInvoices,
                )
            }
        }
    }

    private fun navigateToDetail(invoiceId: String) {
        emitNav(DashboardNavigationEvent.NavigateToInvoiceDetail(invoiceId))
    }

    private fun emitNav(event: DashboardNavigationEvent) {
        viewModelScope.launch { _navigationEvent.send(event) }
    }
}