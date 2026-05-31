package com.app.crowns.history.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.catalog.usecase.GetInvoiceStatusesUseCase
import com.app.crowns.domain.invoice.usecase.GetInvoicesHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getInvoiceStatusesUseCase: GetInvoiceStatusesUseCase,
    private val getInvoicesHistoryUseCase: GetInvoicesHistoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    init {
        loadHistory()
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

    private fun loadHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val statusesResult = getInvoiceStatusesUseCase()
            val statuses = statusesResult.getOrDefault(emptyList())
            
            val invoicesResult = getInvoicesHistoryUseCase()
            val rawInvoices = invoicesResult.getOrDefault(emptyList())

            val invoices = rawInvoices.map { inv ->
                inv.copy(statusInfo = statuses.find { it.code == inv.status })
            }
            
            _state.update {
                it.copy(
                    isLoading = false,
                    invoices = invoices
                )
            }
        }
    }
}
