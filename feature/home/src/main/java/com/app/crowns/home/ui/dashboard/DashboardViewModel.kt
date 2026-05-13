package com.app.crowns.home.ui.dashboard

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
class DashboardViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<DashboardNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadDashboard()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.Refresh -> loadDashboard()
            is DashboardEvent.OnInvoiceClick -> navigateToDetail(event.invoiceId)
            is DashboardEvent.OnNewBoleta -> emitNav(DashboardNavigationEvent.NavigateToNewBoleta)
            is DashboardEvent.OnNewFactura -> emitNav(DashboardNavigationEvent.NavigateToNewFactura)
            is DashboardEvent.OnStockClick -> emitNav(DashboardNavigationEvent.NavigateToStock)
            is DashboardEvent.OnHistoryClick -> emitNav(DashboardNavigationEvent.NavigateToHistory)
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            _state.update {
                it.copy(
                    isLoading = false,
                    userName = "Carlos",
                    todaySales = 1240.00,
                    salesGrowthPercent = 8.0,
                    totalInvoices = 8,
                    acceptedInvoices = 6,
                    pendingInvoices = 2,
                    recentInvoices = listOf(
                        RecentInvoice(
                            id = "1",
                            number = "B001-00045",
                            clientName = "Ferretería López",
                            amount = 85.00,
                            type = InvoiceType.BOLETA,
                            status = InvoiceStatus.ACCEPTED,
                        ),
                        RecentInvoice(
                            id = "2",
                            number = "F001-00122",
                            clientName = "Inversiones Ramos SAC",
                            amount = 420.50,
                            type = InvoiceType.FACTURA,
                            status = InvoiceStatus.PENDING,
                        ),
                        RecentInvoice(
                            id = "3",
                            number = "B001-00044",
                            clientName = "Juan Pérez Quispe",
                            amount = 12.00,
                            type = InvoiceType.BOLETA,
                            status = InvoiceStatus.ACCEPTED,
                        ),
                    ),
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