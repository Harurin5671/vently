package com.app.crowns.history.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.crowns.history.ui.HistoryEvent
import com.app.crowns.history.ui.HistoryUiState
import com.app.crowns.history.ui.groupedInvoices
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun HistoryList(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        state.groupedInvoices.forEach { (groupLabel, invoices) ->
            // Group header
            item(key = "header_$groupLabel") {
                HistoryGroupHeader(label = groupLabel)
            }

            // Invoices in group
            items(invoices, key = { it.id }) { invoice ->
                HistoryInvoiceCard(
                    invoice = invoice,
                    isExpanded = state.expandedInvoiceId == invoice.id,
                    onEvent = onEvent,
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            when {
                state.isLoadingMore -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
                state.hasMorePages -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(
                            onClick = { onEvent(HistoryEvent.OnLoadMore) },
                            modifier = Modifier.padding(vertical = 8.dp),
                        ) {
                            Text("Cargar más")
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}