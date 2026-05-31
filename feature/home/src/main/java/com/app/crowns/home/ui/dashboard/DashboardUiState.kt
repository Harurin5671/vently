package com.app.crowns.home.ui.dashboard

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.invoice.entities.InvoiceEntity

data class DashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val companyInfo: CompanyInfo? = null,
    val todaySales: Double = 0.0,
    val salesGrowthPercent: Double = 0.0,
    val totalInvoices: Int = 0,
    val acceptedInvoices: Int = 0,
    val pendingInvoices: Int = 0,
    val recentInvoices: List<InvoiceEntity> = emptyList(),
    val error: String? = null,
)

// UI specific enums for status/type mapping if needed by the screen
enum class InvoiceStatus { ACCEPTED, PENDING, REJECTED, CANCELLED, DRAFT }
enum class InvoiceType { BOLETA, FACTURA }
