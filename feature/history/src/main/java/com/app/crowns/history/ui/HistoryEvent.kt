package com.app.crowns.history.ui

sealed class HistoryEvent {
    data object Refresh : HistoryEvent()
    data object SilentRefresh : HistoryEvent()
    data object PreviousMonth : HistoryEvent()
    data object NextMonth : HistoryEvent()
    data class OnFilterSelected(val filter: HistoryFilter) : HistoryEvent()
    data class OnInvoiceClick(val invoiceId: String) : HistoryEvent()
    data class OnDownloadPdf(val invoiceId: String) : HistoryEvent()
    data class OnSendInvoice(val invoiceId: String) : HistoryEvent()
    data class OnCancelInvoice(val invoiceId: String) : HistoryEvent()
    data object OnDismissCancelError : HistoryEvent()
    data class OnCancelReasonChanged(val reason: String) : HistoryEvent()
    data object OnConfirmCancelAccepted : HistoryEvent()
    data object OnDismissCancelReason : HistoryEvent()
    data object OnLoadMore : HistoryEvent()
    data object OnDismissLoadError : HistoryEvent()
    data class ExpandInvoice(val invoiceId: String) : HistoryEvent()
}