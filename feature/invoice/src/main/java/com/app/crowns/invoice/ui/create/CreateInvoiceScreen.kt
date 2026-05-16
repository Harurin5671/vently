package com.app.crowns.invoice.ui.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.OnPrimary
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.Surface
import com.app.crowns.designsystem.color.SurfaceContainerLowest
import com.app.crowns.invoice.ui.components.AddProductSheet
import com.app.crowns.invoice.ui.components.ClientSection
import com.app.crowns.invoice.ui.components.InvoiceItemRow
import com.app.crowns.invoice.ui.components.InvoiceSummary
import com.app.crowns.invoice.ui.components.InvoiceTypeSelector

@Composable
fun CreateInvoiceScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPreview: () -> Unit,
    viewModel: CreateInvoiceViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is CreateInvoiceNavigationEvent.NavigateToPreview -> onNavigateToPreview()
                is CreateInvoiceNavigationEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    CreateInvoiceContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateInvoiceContent(
    state: CreateInvoiceUiState,
    onEvent: (CreateInvoiceEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nueva ${state.invoiceType.label}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Button(
                    onClick = { onEvent(CreateInvoiceEvent.OnPreview) },
                    enabled = state.isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryContainer,
                        contentColor = OnPrimary,
                    ),
                ) {
                    Text(
                        text = "Previsualizar comprobante",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Text(
                    text = "Podrás revisar antes de emitir",
                    fontSize = 11.sp,
                    color = Outline,
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(4.dp))

            // Type selector
            InvoiceTypeSelector(
                selected = state.invoiceType,
                onSelect = { onEvent(CreateInvoiceEvent.OnInvoiceTypeChanged(it)) },
            )

            // Client section
            SectionLabel("CLIENTE")
            SectionCard {
                ClientSection(
                    invoiceType = state.invoiceType,
                    clientName = state.clientName,
                    clientRuc = state.clientRuc,
                    clientBusinessName = state.clientBusinessName,
                    isSearchingRuc = state.isSearchingRuc,
                    rucError = state.rucError,
                    onClientNameChanged = { onEvent(CreateInvoiceEvent.OnClientNameChanged(it)) },
                    onClientRucChanged = { onEvent(CreateInvoiceEvent.OnClientRucChanged(it)) },
                    onSearchRuc = { onEvent(CreateInvoiceEvent.OnSearchRuc) },
                )
            }

            // Items section
            SectionLabel("ÍTEMS${if (state.items.isNotEmpty()) " (${state.items.size})" else ""}")
            SectionCard {
                if (state.items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No hay productos agregados",
                            fontSize = 14.sp,
                            color = Outline,
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        state.items.forEachIndexed { index, item ->
                            InvoiceItemRow(
                                item = item,
                                onIncrease = { onEvent(CreateInvoiceEvent.OnIncreaseQuantity(item.productId)) },
                                onDecrease = { onEvent(CreateInvoiceEvent.OnDecreaseQuantity(item.productId)) },
                            )
                            if (index < state.items.lastIndex) {
                                HorizontalDivider(
                                    color = OutlineVariant.copy(alpha = 0.5f),
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Add product button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.5.dp,
                            color = PrimaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onEvent(CreateInvoiceEvent.OnShowAddProductSheet) }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = null,
                        tint = PrimaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "+ Agregar producto",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryContainer,
                    )
                }
            }

            // Summary section
            if (state.items.isNotEmpty()) {
                SectionLabel("RESUMEN")
                SectionCard {
                    InvoiceSummary(
                        subtotal = state.subtotal,
                        igv = state.igv,
                        total = state.total,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // Bottom sheet
    if (state.showAddProductSheet) {
        AddProductSheet(
            searchQuery = state.productSearchQuery,
            selectedCategory = state.selectedCategoryFilter,
            onSearchChanged = { onEvent(CreateInvoiceEvent.OnProductSearchChanged(it)) },
            onCategoryChanged = { onEvent(CreateInvoiceEvent.OnCategoryFilterChanged(it)) },
            onAddItem = { onEvent(CreateInvoiceEvent.OnAddItem(it)) },
            onDismiss = { onEvent(CreateInvoiceEvent.OnDismissAddProductSheet) },
        )
    }
}

// ─────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Outline,
        letterSpacing = 0.8.sp,
    )
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        content()
    }
}