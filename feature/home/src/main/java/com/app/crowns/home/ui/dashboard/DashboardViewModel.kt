package com.app.crowns.home.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.catalog.usecase.GetInvoiceStatusesUseCase
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.invoice.usecase.GetInvoicesHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getInvoicesHistory: GetInvoicesHistoryUseCase,
    private val getInvoiceStatusesUseCase: GetInvoiceStatusesUseCase,
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
            val rawInvoices = invoicesResult.getOrDefault(emptyList())

            val statusesResult = getInvoiceStatusesUseCase()
            val statuses = statusesResult.getOrDefault(emptyList())

            val invoices = rawInvoices.map { inv ->
                inv.copy(statusInfo = statuses.find { it.code == inv.status })
            }

            val now = LocalDate.now()
            val todayStr = now.toString() // "yyyy-MM-dd"
            val monthStr = todayStr.substring(0, 7) // "yyyy-MM"

            val thisMonthInvoices = invoices.filter { it.issueDate.startsWith(monthStr) }
            val todayInvoices = thisMonthInvoices.filter { it.issueDate.startsWith(todayStr) }
            val previousMonthInvoices = thisMonthInvoices.filter { !it.issueDate.startsWith(todayStr) }

            val todaySales = todayInvoices.sumOf { it.total.toDoubleOrNull() ?: 0.0 }
            val previousMonthSales = previousMonthInvoices.sumOf { it.total.toDoubleOrNull() ?: 0.0 }
            
            val growth = if (previousMonthSales > 0) {
                ((todaySales - previousMonthSales) / previousMonthSales) * 100
            } else if (todaySales > 0) {
                100.0 // Si es el primer día con ventas del mes
            } else {
                0.0
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    userName = companyInfo?.businessName ?: "Usuario",
                    companyInfo = companyInfo,
                    todaySales = todaySales,
                    salesGrowthPercent = growth,
                    totalInvoices = thisMonthInvoices.size,
                    acceptedInvoices = thisMonthInvoices.count { inv -> inv.status == InvoiceStatusEntity.ACCEPTED },
                    pendingInvoices = thisMonthInvoices.count { inv ->
                        inv.status == InvoiceStatusEntity.PENDING || 
                        inv.status == InvoiceStatusEntity.DRAFT
                    },
                    recentInvoices = invoices.take(5),
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