package com.app.crowns.home.ui.dashboard

sealed class DashboardEvent {
    data object Refresh : DashboardEvent()
    data class OnInvoiceClick(val invoiceId: String) : DashboardEvent()
    data object OnNewBoleta : DashboardEvent()
    data object OnNewFactura : DashboardEvent()
    data object OnStockClick : DashboardEvent()
    data object OnHistoryClick : DashboardEvent()
}

sealed class DashboardNavigationEvent {
    data object NavigateToNewBoleta : DashboardNavigationEvent()
    data object NavigateToNewFactura : DashboardNavigationEvent()
    data object NavigateToStock : DashboardNavigationEvent()
    data object NavigateToHistory : DashboardNavigationEvent()
    data class NavigateToInvoiceDetail(val invoiceId: String) : DashboardNavigationEvent()
}