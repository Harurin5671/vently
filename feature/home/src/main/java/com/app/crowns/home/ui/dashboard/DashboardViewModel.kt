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
            is DashboardEvent.Refresh -> loadDashboard(isSilent = true)
            is DashboardEvent.OnInvoiceClick -> navigateToDetail(event.invoiceId)
            is DashboardEvent.OnNewBoleta -> emitNav(DashboardNavigationEvent.NavigateToNewBoleta)
            is DashboardEvent.OnNewFactura -> emitNav(DashboardNavigationEvent.NavigateToNewFactura)
            is DashboardEvent.OnStockClick -> emitNav(DashboardNavigationEvent.NavigateToStock)
            is DashboardEvent.OnHistoryClick -> emitNav(DashboardNavigationEvent.NavigateToHistory)
        }
    }

    private fun loadDashboard(isSilent: Boolean = false) {
        viewModelScope.launch {
            android.util.Log.d("DASHBOARD_VM", "Cargando datos del Dashboard...")
            if (!isSilent) _state.update { it.copy(isLoading = true) }

            val companyResult = getCompanyUseCase()
            val companyInfo = companyResult.getOrNull()
            android.util.Log.d("DASHBOARD_VM", "Empresa: ${companyInfo?.businessName}")

            val invoicesResult = getInvoicesHistory()
            val rawInvoices = invoicesResult.getOrDefault(emptyList())
            android.util.Log.d("DASHBOARD_VM", "Facturas obtenidas: ${rawInvoices.size}")

            val statusesResult = getInvoiceStatusesUseCase()
            val statuses = statusesResult.getOrDefault(emptyList())

            val invoices = rawInvoices.map { inv ->
                inv.copy(statusInfo = statuses.find { it.code == inv.status })
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    userName = companyInfo?.businessName ?: "Usuario",
                    companyInfo = companyInfo,
                    todaySales = invoices.sumOf { inv -> inv.total.toDoubleOrNull() ?: 0.0 },
                    salesGrowthPercent = 0.0,
                    totalInvoices = invoices.size,
                    acceptedInvoices = invoices.count { inv -> inv.status == InvoiceStatusEntity.ACCEPTED },
                    pendingInvoices = invoices.count { inv -> 
                        inv.status == InvoiceStatusEntity.PENDING || 
                        inv.status == InvoiceStatusEntity.DRAFT
                    },
                    recentInvoices = invoices.take(5),
                )
            }
            android.util.Log.d("DASHBOARD_VM", "Estado actualizado. Recientes en state: ${_state.value.recentInvoices.size}")
        }
    }

    private fun navigateToDetail(invoiceId: String) {
        emitNav(DashboardNavigationEvent.NavigateToInvoiceDetail(invoiceId))
    }

    private fun emitNav(event: DashboardNavigationEvent) {
        viewModelScope.launch { _navigationEvent.send(event) }
    }
}