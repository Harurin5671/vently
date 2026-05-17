package com.app.crowns.history.domain.model

enum class HistoryInvoiceStatus(val label: String) {
    ACCEPTED("ACEPTADO"),
    PENDING("PENDIENTE"),
    CANCELLED("ANULADO"),
    REJECTED("RECHAZADO"),
}