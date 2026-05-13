package com.app.crowns.home.ui.dashboard

data class DashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val todaySales: Double = 0.0,
    val salesGrowthPercent: Double = 0.0,
    val totalInvoices: Int = 0,
    val acceptedInvoices: Int = 0,
    val pendingInvoices: Int = 0,
    val recentInvoices: List<RecentInvoice> = emptyList(),
    val error: String? = null,
)

data class RecentInvoice(
    val id: String,
    val number: String,
    val clientName: String,
    val amount: Double,
    val type: InvoiceType,
    val status: InvoiceStatus,
)

enum class InvoiceType { BOLETA, FACTURA }

enum class InvoiceStatus { ACCEPTED, PENDING, REJECTED, CANCELLED }