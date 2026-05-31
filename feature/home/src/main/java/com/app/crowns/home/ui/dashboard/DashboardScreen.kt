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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.*
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.home.ui.components.QuickActionCard
import com.app.crowns.home.ui.components.RecentInvoiceItem
import com.app.crowns.home.ui.components.StatCard
import com.app.crowns.ui.components.shimmerEffect
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToNewBoleta: () -> Unit = {},
    onNavigateToNewFactura: () -> Unit = {},
    onNavigateToStock: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToInvoiceDetail: (String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is DashboardNavigationEvent.NavigateToNewBoleta -> onNavigateToNewBoleta()
                is DashboardNavigationEvent.NavigateToNewFactura -> onNavigateToNewFactura()
                is DashboardNavigationEvent.NavigateToStock -> onNavigateToStock()
                is DashboardNavigationEvent.NavigateToHistory -> onNavigateToHistory()
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
        if (state.isLoading) {
            DashboardSkeleton(modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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
                        badge = "+${state.salesGrowthPercent.toInt()}%",
                        badgeColor = com.app.crowns.designsystem.color.Success,
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = "Comprobantes",
                        value = "${state.totalInvoices}",
                        subtitle = "${state.acceptedInvoices} aceptados · ${state.pendingInvoices} pendientes",
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
                        iconTint = androidx.compose.ui.graphics.Color(0xFF00C2CB),
                        iconBackground = androidx.compose.ui.graphics.Color(0xFF00C2CB)
                            .copy(alpha = 0.12f),
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
}

@Composable
private fun GreetingSection(userName: String) {
    val now = LocalDateTime.now()
    val greeting = when (now.hour) {
        in 0..11 -> "Buenos días"
        in 12..17 -> "Buenas tardes"
        else -> "Buenas noches"
    }
    val dayFormat = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "PE"))

    Column {
        Text(
            text = "$greeting, $userName",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface,
        )
        Text(
            text = now.format(dayFormat).replaceFirstChar { it.uppercase() },
            fontSize = 14.sp,
            color = TextMuted,
        )
    }
}

@Composable
private fun DashboardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        
        // Greeting Skeleton
        Column {
            Box(modifier = Modifier.width(200.dp).height(24.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            Spacer(Modifier.height(8.dp))
            Box(modifier = Modifier.width(150.dp).height(16.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        }

        Spacer(Modifier.height(16.dp))

        // Stats Skeleton
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
            Box(modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
        }

        Spacer(Modifier.height(24.dp))

        // Quick Actions Skeleton
        Box(modifier = Modifier.width(120.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
        }

        Spacer(Modifier.height(24.dp))

        // Recent Invoices Skeleton
        Box(modifier = Modifier.width(100.dp).height(20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )
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
