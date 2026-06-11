package com.app.crowns.home.ui.dashboard

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.*
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.home.ui.components.QuickActionCard
import com.app.crowns.home.ui.components.RecentInvoiceItem
import com.app.crowns.home.ui.components.StatCard
import com.app.crowns.home.ui.dashboard.components.DashboardSkeleton
import com.app.crowns.home.ui.dashboard.components.GreetingSection
import com.app.crowns.ui.components.VentlyModalBottomSheet

@Composable
fun DashboardScreen(
    onNavigateToNewBoleta: () -> Unit = {},
    onNavigateToNewFactura: () -> Unit = {},
    onNavigateToStock: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToInvoiceDetail: (String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(DashboardEvent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is DashboardNavigationEvent.NavigateToNewBoleta -> onNavigateToNewBoleta()
                is DashboardNavigationEvent.NavigateToNewFactura -> onNavigateToNewFactura()
                is DashboardNavigationEvent.NavigateToStock -> onNavigateToStock()
                is DashboardNavigationEvent.NavigateToHistory -> onNavigateToHistory()
                is DashboardNavigationEvent.NavigateToSettings -> onNavigateToSettings()
                is DashboardNavigationEvent.NavigateToInvoiceDetail -> onNavigateToInvoiceDetail(
                    event.invoiceId
                )
            }
        }
    }

    DashboardContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardContent(
    state: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
) {
    val locale = LocalConfiguration.current.locales[0]

    Scaffold(
        containerColor = Surface,
        topBar = {
//            DashboardTopAppBaR(companyInfo = state.companyInfo, userName = state.userName)
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading && state.recentInvoices.isNotEmpty(),
            onRefresh = { onEvent(DashboardEvent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading && state.recentInvoices.isEmpty()) {
                DashboardSkeleton()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                ) {
                    Spacer(Modifier.height(16.dp))

                    // Greeting
                    GreetingSection(userName = state.userName)

                Spacer(Modifier.height(16.dp))

                // Stats row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    StatCard(
                        label = "Ventas hoy",
                        value = "S/ ${String.format(locale, "%,.2f", state.todaySales)}",
                        badge = "${if (state.salesGrowthPercent >= 0) "+" else ""}${state.salesGrowthPercent.toInt()}%",
                        badgeColor = if (state.salesGrowthPercent >= 0) Success else Error,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = "Comprobantes (mes)",
                        value = "${state.totalInvoices}",
                        subtitle = "${state.acceptedInvoices} aceptados",
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Quick actions
                Text(
                    text = "Acciones rápidas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface,
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    QuickActionCard(
                        label = "Nueva boleta",
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        iconTint = Primary,
                        iconBackground = PrimaryContainer.copy(alpha = 0.12f),
                        onClick = { onEvent(DashboardEvent.OnNewBoleta) },
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionCard(
                        label = "Nueva factura",
                        icon = Icons.Filled.Business,
                        iconTint = Primary,
                        iconBackground = PrimaryContainer.copy(alpha = 0.12f),
                        onClick = { onEvent(DashboardEvent.OnNewFactura) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    QuickActionCard(
                        label = "Mi stock",
                        icon = Icons.Filled.Inventory2,
                        iconTint = Teal,
                        iconBackground = Teal.copy(alpha = 0.12f),
                        onClick = { onEvent(DashboardEvent.OnStockClick) },
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionCard(
                        label = "Historial",
                        icon = Icons.Filled.History,
                        iconTint = OnSurfaceVariant,
                        iconBackground = OutlineVariant.copy(alpha = 0.2f),
                        onClick = { onEvent(DashboardEvent.OnHistoryClick) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Recent invoices
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Recientes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface,
                    )
                    TextButton(onClick = { onEvent(DashboardEvent.OnHistoryClick) }) {
                        Text(
                            text = "Ver todos",
                            fontSize = 12.sp,
                            color = Primary,
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Invoice list card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp)),
                ) {
                    if (state.recentInvoices.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay comprobantes recientes",
                                fontSize = 14.sp,
                                color = Outline,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    } else {
                        state.recentInvoices.forEachIndexed { index, invoice ->
                            RecentInvoiceItem(
                                invoice = invoice,
                                onClick = { onEvent(DashboardEvent.OnInvoiceClick(invoice.id)) },
                            )
                            if (index < state.recentInvoices.lastIndex) {
                                HorizontalDivider(
                                    color = OutlineVariant.copy(alpha = 0.5f),
                                    thickness = 0.5.dp,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(80.dp)) // espacio para el FAB
            }
        }
    }

    if (state.showMissingDataDialog) {
        VentlyModalBottomSheet(
            onDismiss = { onEvent(DashboardEvent.DismissMissingDataDialog) },
            title = "Información pendiente",
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Para emitir comprobantes electrónicos, necesitas configurar tus credenciales en Ajustes.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = OnSurface
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val missingItems = mutableListOf<String>()
                    if (state.companyInfo?.certificateBase64 == null) missingItems.add("Certificado digital")
                    if (state.companyInfo?.certificatePassword == null) missingItems.add("Contraseña del certificado")
                    if (state.companyInfo?.solUsername == null || state.companyInfo.solPassword == null) missingItems.add("Usuario y contraseña SOL")

                    missingItems.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onEvent(DashboardEvent.OnGoToSettings) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Configurar ahora", color = OnPrimary, fontWeight = FontWeight.SemiBold)
                }

                TextButton(
                    onClick = { onEvent(DashboardEvent.DismissMissingDataDialog) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ahora no", color = OnSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
}

@ThemePreviews
@Composable
fun DashboardScreenPreview() {
    VentlyTheme {
        DashboardContent(
            state = DashboardUiState(
                userName = "Juan Pérez",
                todaySales = 1250.50,
                salesGrowthPercent = 15.0,
                totalInvoices = 45,
                acceptedInvoices = 42,
                pendingInvoices = 3,
                recentInvoices = emptyList() // Updated to match entity list
            ),
            onEvent = {}
        )
    }
}
