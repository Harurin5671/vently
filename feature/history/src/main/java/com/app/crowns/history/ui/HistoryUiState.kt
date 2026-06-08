package com.app.crowns.history.ui

import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HistoryUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedFilter: HistoryFilter = HistoryFilter.ALL,
    val invoices: List<InvoiceEntity> = emptyList(),
    val companyInfo: CompanyInfo? = null,
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
        // Parse date - handles ISO like "2023-10-27T10:00:00" or simple date "2023-10-27"
        val dateStr = invoice.issueDate.take(10)
        val date = try { LocalDate.parse(dateStr) } catch (e: Exception) { LocalDate.now() }
        val month = YearMonth.from(date)
        
        val matchesMonth = month == currentMonth
        val matchesFilter = when (selectedFilter) {
            HistoryFilter.ALL -> true
            HistoryFilter.BOLETA -> invoice.documentType == "03"
            HistoryFilter.FACTURA -> invoice.documentType == "01"
            HistoryFilter.ACCEPTED -> invoice.status == InvoiceStatusEntity.ACCEPTED
            HistoryFilter.PENDING -> invoice.status == InvoiceStatusEntity.PENDING || invoice.status == InvoiceStatusEntity.DRAFT
        }
        matchesMonth && matchesFilter
    }

// Agrupa por fecha para mostrar HOY, AYER, etc.
val HistoryUiState.groupedInvoices: Map<String, List<InvoiceEntity>>
    get() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "PE"))
        
        return filteredInvoices
            .sortedByDescending { it.issueDate }
            .groupBy { invoice ->
                val dateStr = invoice.issueDate.take(10)
                val date = try { LocalDate.parse(dateStr) } catch (e: Exception) { today }
                
                when (date) {
                    today -> "HOY"
                    yesterday -> "AYER"
                    else -> date.format(dateFormatter).uppercase()
                }
            }
    }
