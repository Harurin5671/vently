package com.app.crowns.history.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.domain.util.shareInvoice
import com.app.crowns.domain.util.openFile
import com.app.crowns.domain.util.copyFileToUri
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.SurfaceContainerHigh
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.documentName
import com.app.crowns.domain.invoice.entities.formattedId
import com.app.crowns.history.ui.components.HistoryEmptyState
import java.io.File
import com.app.crowns.history.ui.components.HistoryInvoiceCard
import com.app.crowns.history.ui.components.HistorySkeletonList
import com.app.crowns.history.ui.components.HistoryTopBar

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    var pendingFileToDownload by remember { mutableStateOf<File?>(null) }
    
    val downloadLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        uri?.let {
            pendingFileToDownload?.let { file ->
                context.copyFileToUri(file, it)
                pendingFileToDownload = null
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HistoryEffect.ShareInvoice -> {
                    val company = state.companyInfo
                    context.shareInvoice(
                        companyName = company?.businessName ?: "Vently App",
                        companyRuc = company?.documentNumber ?: "",
                        documentName = effect.invoice.documentName,
                        documentId = effect.invoice.formattedId,
                        customerName = effect.invoice.customerLegalName,
                        total = effect.invoice.total,
                        pdfFile = effect.pdfFile
                    )
                }
                is HistoryEffect.OpenPdf -> {
                    context.openFile(effect.file)
                }
                is HistoryEffect.DownloadPdf -> {
                    pendingFileToDownload = effect.file
                    downloadLauncher.launch(effect.fileName)
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(HistoryEvent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
    val filteredInvoices = state.filteredInvoices
    
    Scaffold(
        containerColor = SurfaceContainerHigh,
        topBar = {
            HistoryTopBar(
                state = state,
                onEvent = onEvent
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading && state.invoices.isNotEmpty(),
            onRefresh = { onEvent(HistoryEvent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading && state.invoices.isEmpty() -> {
                    HistorySkeletonList()
                }
                filteredInvoices.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        HistoryEmptyState(currentMonth = state.currentMonth)
                    }
                }
                else -> {
                    HistoryList(
                        state = state,
                        onEvent = onEvent,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryList(
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

        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun HistoryGroupHeader(label: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainerHigh.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Outline,
            letterSpacing = 0.8.sp,
        )
    }
}

// ─────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────

@ThemePreviews
@Composable
private fun HistoryEmptyPreview() {
    VentlyTheme {
        HistoryContent(
            state = HistoryUiState(),
            onEvent = {}
        )
    }
}

@ThemePreviews
@Composable
private fun HistoryWithItemsPreview() {
    val mockInvoices = listOf(
        InvoiceEntity(
            id = "1",
            companyId = "comp1",
            documentType = "03",
            serie = "B001",
            correlative = 1,
            total = "50.0",
            status = InvoiceStatusEntity.ACCEPTED,
            issueDate = java.time.LocalDateTime.now().toString(),
            operationType = "01",
            currency = "PEN",
            paymentType = "01",
            totalDiscount = "0.0",
            totalExportacion = "0.0",
            totalExonerada = "0.0",
            totalInafecta = "0.0",
            totalGravada = "42.37",
            totalIgv = "7.63",
            xmlDocument = "",
            items = emptyList(),
            customerDocNumber = "12345678",
            customerLegalName = "Juan Pérez"
        ),
        InvoiceEntity(
            id = "2",
            companyId = "comp1",
            documentType = "01",
            serie = "F001",
            correlative = 1,
            total = "118.0",
            status = InvoiceStatusEntity.PENDING,
            issueDate = java.time.LocalDateTime.now().minusDays(1).toString(),
            operationType = "01",
            currency = "PEN",
            paymentType = "01",
            totalDiscount = "0.0",
            totalExportacion = "0.0",
            totalExonerada = "0.0",
            totalInafecta = "0.0",
            totalGravada = "100.0",
            totalIgv = "18.0",
            xmlDocument = "",
            items = emptyList(),
            customerDocNumber = "20601234567",
            customerLegalName = "Empresa S.A.C."
        )
    )

    VentlyTheme {
        HistoryContent(
            state = HistoryUiState(
                invoices = mockInvoices
            ),
            onEvent = {}
        )
    }
}

@ThemePreviews
@Composable
private fun HistoryExpandedPreview() {
    val mockInvoices = listOf(
        InvoiceEntity(
            id = "1",
            companyId = "comp1",
            documentType = "03",
            serie = "B001",
            correlative = 1,
            total = "50.0",
            status = InvoiceStatusEntity.ACCEPTED,
            issueDate = java.time.LocalDateTime.now().toString(),
            operationType = "01",
            currency = "PEN",
            paymentType = "01",
            totalDiscount = "0.0",
            totalExportacion = "0.0",
            totalExonerada = "0.0",
            totalInafecta = "0.0",
            totalGravada = "42.37",
            totalIgv = "7.63",
            xmlDocument = "",
            items = emptyList(),
            customerDocNumber = "12345678",
            customerLegalName = "Juan Pérez"
        )
    )

    VentlyTheme {
        HistoryContent(
            state = HistoryUiState(
                invoices = mockInvoices,
                expandedInvoiceId = "1"
            ),
            onEvent = {}
        )
    }
}
