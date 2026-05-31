package com.app.crowns.history.ui

import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import java.time.YearMonth

data class HistoryUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val invoices: List<InvoiceEntity> = emptyList(),
    val expandedInvoiceId: String? = null,
    val isLoading: Boolean = false,
)

enum class HistoryFilter(val label: String) {
    ALL("Todos"),
    BOLETA("Boletas"),
    FACTURA("Facturas"),
    ACCEPTED("Aceptadas"),
    PENDING("Pendientes"),
}

val HistoryUiState.filteredInvoices: List<InvoiceEntity>
    get() = invoices.filter { invoice ->
        when (selectedFilter) {
            HistoryFilter.ALL -> true
            HistoryFilter.BOLETA -> invoice.documentType == "03"
            HistoryFilter.FACTURA -> invoice.documentType == "01"
            HistoryFilter.ACCEPTED -> invoice.status == InvoiceStatusEntity.ACCEPTED
            HistoryFilter.PENDING -> invoice.status == InvoiceStatusEntity.PENDING || invoice.status == InvoiceStatusEntity.DRAFT
        }
    }

// Agrupa por fecha para mostrar HOY, AYER, etc.
val HistoryUiState.groupedInvoices: Map<String, List<InvoiceEntity>>
    get() {
        val today = java.time.LocalDate.now()
        val yesterday = today.minusDays(1)
        return filteredInvoices.groupBy { invoice ->
            when (val invoiceDate = invoice.issueDate.take(10)) {
                today.toString() -> "HOY"
                yesterday.toString() -> "AYER"
                else -> invoiceDate
            }
        }
    }
