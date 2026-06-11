package com.app.crowns.invoice.domain.store

import android.content.Context
import com.app.crowns.invoice.domain.model.Invoice
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_NAME = "vently_invoice_draft"
private const val KEY_DRAFT = "draft_json"

@Singleton
class InvoiceDraftStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _draft = MutableStateFlow<Invoice?>(restoreFromDisk())
    val draft: StateFlow<Invoice?> = _draft.asStateFlow()

    fun save(invoice: Invoice) {
        _draft.value = invoice
        prefs.edit().putString(KEY_DRAFT, Json.encodeToString(invoice)).apply()
    }

    fun clear() {
        _draft.value = null
        prefs.edit().remove(KEY_DRAFT).apply()
    }

    private fun restoreFromDisk(): Invoice? {
        val json = prefs.getString(KEY_DRAFT, null) ?: return null
        return runCatching { Json.decodeFromString<Invoice>(json) }.getOrNull()
    }
}
