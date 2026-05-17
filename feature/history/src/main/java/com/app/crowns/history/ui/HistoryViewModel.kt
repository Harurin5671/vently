package com.app.crowns.history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.history.domain.model.HistoryInvoice
import com.app.crowns.history.domain.model.HistoryInvoiceItem
import com.app.crowns.history.domain.model.HistoryInvoiceStatus
import com.app.crowns.history.domain.model.HistoryInvoiceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init {
        loadMockData()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.PreviousMonth -> _state.update {
                it.copy(currentMonth = it.currentMonth.minusMonths(1))
            }
            is HistoryEvent.NextMonth -> _state.update {
                it.copy(currentMonth = it.currentMonth.plusMonths(1))
            }
            is HistoryEvent.OnFilterSelected -> _state.update {
                it.copy(selectedFilter = event.filter)
            }
            is HistoryEvent.OnInvoiceClick -> _state.update {
                val newId = if (it.expandedInvoiceId == event.invoiceId) null else event.invoiceId
                it.copy(expandedInvoiceId = newId)
            }
            is HistoryEvent.OnDownloadPdf -> { /* implementar con lógica real */ }
            is HistoryEvent.OnSendInvoice -> { /* implementar con lógica real */ }
            is HistoryEvent.OnCancelInvoice -> { /* implementar con lógica real */ }
            is HistoryEvent.OnCopyHash -> { /* implementar con clipboard */ }
        }
    }

    private fun loadMockData() {
        viewModelScope.launch {
            val today = java.time.LocalDate.now().toString()
            val yesterday = java.time.LocalDate.now().minusDays(1).toString()
            _state.update {
                it.copy(
                    invoices = listOf(
                        HistoryInvoice(
                            id = "1",
                            number = "B001-00001",
                            clientName = "Juan Perez Lopez",
                            clientRuc = "10456789012",
                            amount = 85.00,
                            subtotal = 72.03,
                            igv = 12.97,
                            type = HistoryInvoiceType.BOLETA,
                            status = HistoryInvoiceStatus.ACCEPTED,
                            dateTime = "${today}T14:20:00",
                            items = listOf(
                                HistoryInvoiceItem("Consultoría Tech", 1, 50.00),
                                HistoryInvoiceItem("Licencia Soft", 2, 17.50),
                            ),
                            hash = "8k1v9u...r5t2",
                        ),
                        HistoryInvoice(
                            id = "2",
                            number = "F001-00245",
                            clientName = "Inversiones Global SAC",
                            clientRuc = "20456789012",
                            amount = 1234.56,
                            subtotal = 1046.24,
                            igv = 188.32,
                            type = HistoryInvoiceType.FACTURA,
                            status = HistoryInvoiceStatus.PENDING,
                            dateTime = "${today}T10:15:00",
                            items = listOf(
                                HistoryInvoiceItem("Servicio Consultoría", 3, 411.52),
                            ),
                            hash = null,
                        ),
                        HistoryInvoice(
                            id = "3",
                            number = "B001-00000",
                            clientName = "Maria Rodriguez",
                            clientRuc = null,
                            amount = 42.50,
                            subtotal = 36.02,
                            igv = 6.48,
                            type = HistoryInvoiceType.BOLETA,
                            status = HistoryInvoiceStatus.ACCEPTED,
                            dateTime = "${yesterday}T09:30:00",
                            items = listOf(
                                HistoryInvoiceItem("Producto A", 1, 42.50),
                            ),
                            hash = "9x2m4p...k8w1",
                        ),
                        HistoryInvoice(
                            id = "4",
                            number = "B001-00129",
                            clientName = "Consumidor Final",
                            clientRuc = null,
                            amount = 15.00,
                            subtotal = 12.71,
                            igv = 2.29,
                            type = HistoryInvoiceType.BOLETA,
                            status = HistoryInvoiceStatus.CANCELLED,
                            dateTime = "${yesterday}T16:45:00",
                            items = listOf(
                                HistoryInvoiceItem("Producto B", 1, 15.00),
                            ),
                            hash = null,
                        ),
                    )
                )
            }
        }
    }
}