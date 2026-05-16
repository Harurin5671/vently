package com.app.crowns.invoice.domain.store

import com.app.crowns.invoice.domain.model.Invoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceDraftStore @Inject constructor() {

    private val _draft = MutableStateFlow<Invoice?>(null)
    val draft: StateFlow<Invoice?> = _draft.asStateFlow()

    fun save(invoice: Invoice) {
        _draft.value = invoice
    }

    fun clear() {
        _draft.value = null
    }
}