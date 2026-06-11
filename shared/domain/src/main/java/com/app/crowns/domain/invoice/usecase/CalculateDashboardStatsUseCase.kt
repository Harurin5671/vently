package com.app.crowns.domain.invoice.usecase

import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import java.time.LocalDate
import javax.inject.Inject

data class DashboardStats(
    val todaySales: Double,
    val salesGrowthPercent: Double,
    val totalInvoices: Int,
    val acceptedInvoices: Int,
    val pendingInvoices: Int,
    val recentInvoices: List<InvoiceEntity>,
)

class CalculateDashboardStatsUseCase @Inject constructor() {
    operator fun invoke(invoices: List<InvoiceEntity>): DashboardStats {
        val now = LocalDate.now()
        val todayStr = now.toString()
        val monthStr = todayStr.substring(0, 7)

        val thisMonthInvoices = invoices.filter { it.issueDate.startsWith(monthStr) }
        val todayInvoices = thisMonthInvoices.filter { it.issueDate.startsWith(todayStr) }
        val restOfMonthInvoices = thisMonthInvoices.filter { !it.issueDate.startsWith(todayStr) }

        val todaySales = todayInvoices.sumOf { it.total.toDoubleOrNull() ?: 0.0 }
        val restOfMonthSales = restOfMonthInvoices.sumOf { it.total.toDoubleOrNull() ?: 0.0 }

        val growth = when {
            restOfMonthSales > 0 -> ((todaySales - restOfMonthSales) / restOfMonthSales) * 100
            todaySales > 0 -> 100.0
            else -> 0.0
        }

        return DashboardStats(
            todaySales = todaySales,
            salesGrowthPercent = growth,
            totalInvoices = thisMonthInvoices.size,
            acceptedInvoices = thisMonthInvoices.count { it.status == InvoiceStatusEntity.ACCEPTED },
            pendingInvoices = thisMonthInvoices.count {
                it.status == InvoiceStatusEntity.PENDING || it.status == InvoiceStatusEntity.DRAFT
            },
            recentInvoices = invoices.take(5),
        )
    }
}
