package com.app.crowns.history.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.util.generateInvoicePdf
import com.app.crowns.domain.catalog.usecase.GetInvoiceStatusesUseCase
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.usecase.GetInvoicesHistoryUseCase
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
            is HistoryEvent.PreviousMonth -> _state.update {
                it.copy(currentMonth = it.currentMonth.minusMonths(1))
            }
            is HistoryEvent.NextMonth -> _state.update {
                if (it.currentMonth < java.time.YearMonth.now()) {
                    it.copy(currentMonth = it.currentMonth.plusMonths(1))
                } else it
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
            is HistoryEvent.OnCancelInvoice -> { /* implementar con lógica real */ }
        }
    }

    private fun loadCompanyInfo() {
        viewModelScope.launch {
            getCompanyUseCase().onSuccess { company ->
                _state.update { it.copy(companyInfo = company) }
            }
        }
    }

    private fun loadHistory(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent) _state.update { it.copy(isLoading = true) }
            
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
