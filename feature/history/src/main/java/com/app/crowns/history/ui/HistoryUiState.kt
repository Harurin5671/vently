package com.app.crowns.history.ui

import com.app.crowns.history.domain.model.HistoryInvoice
import com.app.crowns.history.domain.model.HistoryInvoiceStatus
import com.app.crowns.history.domain.model.HistoryInvoiceType
import java.time.YearMonth

data class HistoryUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val invoices: List<HistoryInvoice> = emptyList(),
    val expandedInvoiceId: String? = null,
    val isLoading: Boolean = false,
)

enum class HistoryFilter(val label: String) {
    ALL("Todos"),
    BOLETA("Boletas"),
    FACTURA("Facturas"),
    ACCEPTED("Aceptados"),
    PENDING("Pendientes"),
}

val HistoryUiState.filteredInvoices: List<HistoryInvoice>
    get() = invoices.filter { invoice ->
        when (selectedFilter) {
            HistoryFilter.ALL -> true
            HistoryFilter.BOLETA -> invoice.type == HistoryInvoiceType.BOLETA
            HistoryFilter.FACTURA -> invoice.type == HistoryInvoiceType.FACTURA
            HistoryFilter.ACCEPTED -> invoice.status == HistoryInvoiceStatus.ACCEPTED
            HistoryFilter.PENDING -> invoice.status == HistoryInvoiceStatus.PENDING
        }
    }

// Agrupa por fecha para mostrar HOY, AYER, etc.
val HistoryUiState.groupedInvoices: Map<String, List<HistoryInvoice>>
    get() {
        val today = java.time.LocalDate.now()
        val yesterday = today.minusDays(1)
        return filteredInvoices.groupBy { invoice ->
            val invoiceDate = invoice.dateTime.take(10)
            when (invoiceDate) {
                today.toString() -> "HOY"
                yesterday.toString() -> "AYER"
                else -> invoiceDate
            }
        }
    }