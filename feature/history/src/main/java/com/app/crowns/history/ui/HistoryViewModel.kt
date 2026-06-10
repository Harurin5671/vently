package com.app.crowns.history.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.util.generateInvoicePdf
import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.catalog.usecase.GetInvoiceStatusesUseCase
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.usecase.CancelAcceptedInvoiceUseCase
import com.app.crowns.domain.invoice.usecase.CancelInvoiceUseCase
import com.app.crowns.domain.invoice.usecase.GetInvoicesHistoryUseCase
import com.app.crowns.domain.invoice.usecase.GetInvoicesHistoryUseCase.Companion.PAGE_SIZE
import java.time.YearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

sealed class HistoryEffect {
    data class ShareInvoice(val invoice: InvoiceEntity, val pdfFile: File) : HistoryEffect()
    data class OpenPdf(val file: File) : HistoryEffect()
    data class DownloadPdf(val file: File, val fileName: String) : HistoryEffect()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getInvoiceStatusesUseCase: GetInvoiceStatusesUseCase,
    private val getInvoicesHistoryUseCase: GetInvoicesHistoryUseCase,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val cancelInvoiceUseCase: CancelInvoiceUseCase,
    private val cancelAcceptedInvoiceUseCase: CancelAcceptedInvoiceUseCase,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    private val _effect = Channel<HistoryEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadHistory()
        loadCompanyInfo()
    }

    fun onEvent(event: HistoryEvent) {
        when (event) {
            is HistoryEvent.Refresh -> {
                loadHistory(isSilent = false)
                loadCompanyInfo()
            }
            is HistoryEvent.PreviousMonth -> {
                val newMonth = _state.value.currentMonth.minusMonths(1)
                _state.update { it.copy(currentMonth = newMonth) }
                loadHistory(month = newMonth)
            }
            is HistoryEvent.NextMonth -> {
                val current = _state.value.currentMonth
                if (current < YearMonth.now()) {
                    val newMonth = current.plusMonths(1)
                    _state.update { it.copy(currentMonth = newMonth) }
                    loadHistory(month = newMonth)
                }
            }
            is HistoryEvent.OnFilterSelected -> _state.update {
                it.copy(selectedFilter = event.filter)
            }
            is HistoryEvent.OnInvoiceClick -> _state.update {
                val newId = if (it.expandedInvoiceId == event.invoiceId) null else event.invoiceId
                it.copy(expandedInvoiceId = newId)
            }
            is HistoryEvent.OnDownloadPdf -> { 
                viewModelScope.launch {
                    _state.value.invoices.find { it.id == event.invoiceId }?.let { invoice ->
                        val pdfFile = withContext(Dispatchers.IO) {
                            generateInvoicePdf(context, invoice, _state.value.companyInfo)
                        }
                        val fileName = "Comprobante_${invoice.serie}_${invoice.correlative}.pdf"
                        _effect.send(HistoryEffect.DownloadPdf(pdfFile, fileName))
                    }
                }
            }
            is HistoryEvent.OnSendInvoice -> {
                viewModelScope.launch {
                    val invoice = _state.value.invoices.find { it.id == event.invoiceId }
                    if (invoice != null) {
                        val pdfFile = withContext(Dispatchers.IO) {
                            generateInvoicePdf(context, invoice, _state.value.companyInfo)
                        }
                        _effect.send(HistoryEffect.ShareInvoice(invoice, pdfFile))
                    }
                }
            }
            is HistoryEvent.OnCancelInvoice -> cancelInvoice(event.invoiceId)
            is HistoryEvent.OnDismissCancelError -> _state.update { it.copy(cancelError = null) }
            is HistoryEvent.OnCancelReasonChanged -> _state.update { it.copy(cancelReasonInput = event.reason) }
            is HistoryEvent.OnConfirmCancelAccepted -> confirmCancelAccepted()
            is HistoryEvent.OnDismissCancelReason -> _state.update { it.copy(showCancelReasonDialog = false, pendingCancelInvoiceId = null, cancelReasonInput = "") }
            is HistoryEvent.OnLoadMore -> loadMoreHistory()
        }
    }

    private fun loadCompanyInfo() {
        viewModelScope.launch {
            getCompanyUseCase().onSuccess { company ->
                _state.update { it.copy(companyInfo = company) }
            }
        }
    }

    private fun cancelInvoice(invoiceId: String) {
        val invoice = _state.value.invoices.find { it.id == invoiceId } ?: return
        if (invoice.status == InvoiceStatusEntity.ACCEPTED || invoice.status == InvoiceStatusEntity.OBSERVED) {
            _state.update { it.copy(showCancelReasonDialog = true, pendingCancelInvoiceId = invoiceId, cancelReasonInput = "") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(cancellingInvoiceId = invoiceId) }
            cancelInvoiceUseCase(invoiceId)
                .onSuccess { loadHistory(isSilent = true) }
                .onFailure { _state.update { it.copy(cancelError = "No se pudo anular el comprobante. Intenta de nuevo.") } }
            _state.update { it.copy(cancellingInvoiceId = null) }
        }
    }

    private fun confirmCancelAccepted() {
        val invoiceId = _state.value.pendingCancelInvoiceId ?: return
        val reason = _state.value.cancelReasonInput.trim()
        if (reason.isBlank()) {
            _state.update { it.copy(cancelError = "Debes ingresar el motivo de anulación") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(cancellingInvoiceId = invoiceId, showCancelReasonDialog = false, pendingCancelInvoiceId = null, cancelReasonInput = "") }
            cancelAcceptedInvoiceUseCase(invoiceId, reason)
                .onSuccess { response ->
                    if (response.success) {
                        loadHistory(isSilent = true)
                    } else {
                        _state.update { it.copy(cancelError = response.sunatDescription ?: "SUNAT rechazó la comunicación de baja") }
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(cancelError = e.message ?: "No se pudo enviar la comunicación de baja a SUNAT") }
                }
            _state.update { it.copy(cancellingInvoiceId = null) }
        }
    }

    private fun loadHistory(isSilent: Boolean = false, month: YearMonth = _state.value.currentMonth) {
        viewModelScope.launch {
            if (!isSilent) _state.update { it.copy(isLoading = true, invoices = emptyList(), hasMorePages = false) }
            val statuses = getInvoiceStatusesUseCase().getOrDefault(emptyList())
            val rawInvoices = getInvoicesHistoryUseCase(month, offset = 0).getOrDefault(emptyList())
            val invoices = rawInvoices.map { inv -> inv.copy(statusInfo = statuses.find { it.code == inv.status }) }
            _state.update {
                it.copy(
                    isLoading = false,
                    invoices = invoices,
                    hasMorePages = rawInvoices.size == GetInvoicesHistoryUseCase.PAGE_SIZE,
                )
            }
        }
    }

    private fun loadMoreHistory() {
        if (_state.value.isLoadingMore || !_state.value.hasMorePages) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            val offset = _state.value.invoices.size
            val statuses = getInvoiceStatusesUseCase().getOrDefault(emptyList())
            val rawInvoices = getInvoicesHistoryUseCase(_state.value.currentMonth, offset).getOrDefault(emptyList())
            val newInvoices = rawInvoices.map { inv -> inv.copy(statusInfo = statuses.find { it.code == inv.status }) }
            _state.update {
                it.copy(
                    isLoadingMore = false,
                    invoices = it.invoices + newInvoices,
                    hasMorePages = rawInvoices.size == GetInvoicesHistoryUseCase.PAGE_SIZE,
                )
            }
        }
    }
}
