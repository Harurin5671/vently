package com.app.crowns.history.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.OnSurfaceVariant
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Surface
import com.app.crowns.designsystem.color.SurfaceContainerHigh
import com.app.crowns.designsystem.color.SurfaceContainerLowest
import com.app.crowns.history.domain.model.HistoryInvoice
import com.app.crowns.history.ui.components.HistoryDateSelector
import com.app.crowns.history.ui.components.HistoryFilterChips
import com.app.crowns.history.ui.components.HistoryInvoiceDetail
import com.app.crowns.history.ui.components.HistoryInvoiceItem

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HistoryContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryContent(
    state: HistoryUiState,
    onEvent: (HistoryEvent) -> Unit,
) {
    Scaffold(
        containerColor = SurfaceContainerHigh,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Historial",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface,
                        )
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = "Filtrar",
                                tint = OnSurface,
                            )
                        }
                        // Avatar placeholder
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh)
                                .border(1.dp, OutlineVariant, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "U",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnSurfaceVariant,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
                )

                // Date selector + filters
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Surface)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    HistoryDateSelector(
                        currentMonth = state.currentMonth,
                        onPrevious = { onEvent(HistoryEvent.PreviousMonth) },
                        onNext = { onEvent(HistoryEvent.NextMonth) },
                    )
                    HistoryFilterChips(
                        selectedFilter = state.selectedFilter,
                        onFilterSelected = { onEvent(HistoryEvent.OnFilterSelected(it)) },
                    )
                }
            }
        }
    ) { innerPadding ->
        if (state.groupedInvoices.isEmpty()) {
            EmptyHistory(modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(0.dp),
            ) {
                state.groupedInvoices.forEach { (groupLabel, invoices) ->
                    // Group header
                    item(key = "header_$groupLabel") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceContainerHigh.copy(alpha = 0.9f))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                        ) {
                            Text(
                                text = groupLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Outline,
                                letterSpacing = 0.8.sp,
                            )
                        }
                    }

                    // Invoices in group
                    items(invoices, key = { it.id }) { invoice ->
                        InvoiceCard(
                            invoice = invoice,
                            isExpanded = state.expandedInvoiceId == invoice.id,
                            onEvent = onEvent,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun InvoiceCard(
    invoice: HistoryInvoice,
    isExpanded: Boolean,
    onEvent: (HistoryEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp)),
    ) {
        HistoryInvoiceItem(
            invoice = invoice,
            onClick = { onEvent(HistoryEvent.OnInvoiceClick(invoice.id)) },
        )
        if (isExpanded) {
            HistoryInvoiceDetail(
                invoice = invoice,
                onDownloadPdf = { onEvent(HistoryEvent.OnDownloadPdf(invoice.id)) },
                onSend = { onEvent(HistoryEvent.OnSendInvoice(invoice.id)) },
                onCancel = { onEvent(HistoryEvent.OnCancelInvoice(invoice.id)) },
                onCopyHash = { onEvent(HistoryEvent.OnCopyHash(it)) },
            )
        }
    }
}

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "📄",
                fontSize = 48.sp,
            )
            Text(
                text = "No hay comprobantes",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
            )
            Text(
                text = "Los comprobantes emitidos aparecerán aquí",
                fontSize = 14.sp,
                color = Outline,
            )
        }
    }
}