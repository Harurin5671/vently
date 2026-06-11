package com.app.crowns.invoice.ui.preview

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.*
import com.app.crowns.invoice.ui.InvoiceResultScreen
import com.app.crowns.invoice.ui.InvoiceSendingScreen

@Composable
fun InvoicePreviewScreen(
    onNavigateBack: () -> Unit,
    onInvoiceEmitted: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: InvoicePreviewViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is InvoicePreviewNavigationEvent.NavigateBack -> onNavigateBack()
                is InvoicePreviewNavigationEvent.InvoiceEmitted -> onInvoiceEmitted()
            }
        }
    }

    Crossfade(targetState = state.showResultScreen to state.isEmitting) { (showResult, isEmitting) ->
        when {
            showResult -> {
                InvoiceResultScreen(
                    state = state,
                    onClose = onNavigateBack,
                    onNavigateToHistory = onNavigateToHistory,
                    onFinish = onInvoiceEmitted
                )
            }
            isEmitting -> {
                InvoiceSendingScreen()
            }
            else -> {
                InvoicePreviewContent(
                    state = state,
                    snackbarHostState = snackbarHostState,
                    onNavigateBack = onNavigateBack,
                    onShowEmitDialog = viewModel::onShowEmitDialog,
                    onDismissEmitDialog = viewModel::onDismissEmitDialog,
                    onConfirmEmit = viewModel::onConfirmEmit,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoicePreviewContent(
    state: InvoicePreviewUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onShowEmitDialog: () -> Unit,
    onDismissEmitDialog: () -> Unit,
    onConfirmEmit: () -> Unit,
) {
    Scaffold(
        containerColor = Surface,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Vista previa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
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
                actions = { Spacer(Modifier.size(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceContainerLowest),
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLowest)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(0.42f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryContainer),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = PrimaryContainer,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "Editar",
                        color = PrimaryContainer,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Button(
                    onClick = onShowEmitDialog,
                    enabled = !state.isGeneratingPdf && !state.isEmitting,
                    modifier = Modifier
                        .weight(0.58f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryContainer,
                        contentColor = OnPrimary,
                    ),
                ) {
                    if (state.isEmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = OnPrimary,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = "Emitir",
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Warning banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(StockLowBg)
                    .border(
                        width = 3.dp,
                        color = StockLow,
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            bottomStart = 12.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp,
                        )
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = StockLow,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Revisa el comprobante antes de emitir. Una vez enviado a SUNAT no podrá modificarse.",
                    fontSize = 12.sp,
                    color = WarningDark,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp,
                )
            }

            // PDF preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerLowest)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    state.isGeneratingPdf -> {
                        Column(
                            modifier = Modifier.padding(48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            CircularProgressIndicator(color = Primary)
                            Text(
                                text = "Generando vista previa...",
                                fontSize = 14.sp,
                                color = Outline,
                            )
                        }
                    }
                    state.pdfPages.isNotEmpty() -> {
                        Column {
                            state.pdfPages.forEach { bitmap ->
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = "Vista previa del comprobante",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.FillWidth,
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = state.error ?: "No se pudo generar la vista previa",
                            fontSize = 14.sp,
                            color = ErrorBright,
                            modifier = Modifier.padding(48.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }

    // Emit confirmation dialog
    if (state.showEmitDialog) {
        AlertDialog(
            onDismissRequest = onDismissEmitDialog,
            title = {
                Text(
                    text = "¿Emitir este comprobante?",
                    fontWeight = FontWeight.SemiBold,
                )
            },
            text = {
                Text(
                    text = "Se enviará a SUNAT y tendrá validez legal inmediata. No podrás anularlo directamente una vez emitido.",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirmEmit,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                ) {
                    Text("Confirmar emisión", color = OnPrimary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissEmitDialog,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancelar", color = Outline)
                }
            },
            containerColor = Surface,
            shape = RoundedCornerShape(20.dp),
        )
    }
}