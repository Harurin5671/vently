package com.app.crowns.invoice.ui.preview

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.OnPrimary
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.OnSurfaceVariant
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.Surface
import com.app.crowns.designsystem.color.SurfaceContainerHigh
import com.app.crowns.designsystem.color.SurfaceContainerLowest
import java.time.LocalDate

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
                    .background(Color(0xFFFFFBEB))
                    .border(
                        width = 3.dp,
                        color = Color(0xFFF59E0B),
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
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Revisa el comprobante antes de emitir. Una vez enviado a SUNAT no podrá modificarse.",
                    fontSize = 12.sp,
                    color = Color(0xFF92400E),
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
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp)),
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
                            color = Color(0xFFEF4444),
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
                    color = Color(0xFF484556),
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

@Composable
private fun InvoiceSendingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface),
        contentAlignment = Alignment.Center
    ) {
        // Decorative top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.TopCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Primary, PrimaryContainer, Primary)
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = PrimaryContainer,
                    strokeWidth = 4.dp
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Enviando a SUNAT...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface
                )
                Text(
                    text = "Esto puede tardar unos segundos",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Illustration placeholder
            Image(
                imageVector = Icons.Default.Warning, // Replace with proper illustration if available
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .alpha(0.1f),
                contentScale = ContentScale.Fit
            )
        }

        // Branding
        Text(
            text = "Vently",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(0.2f),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InvoiceResultScreen(
    state: InvoicePreviewUiState,
    onClose: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onFinish: () -> Unit
) {
    val isSuccess = state.sunatResponse?.isSuccess == true

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isSuccess) "Comprobante emitido" else "Error en emisión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = { Spacer(Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PrimaryContainer)
                ) {
                    Text("Ver en historial", color = PrimaryContainer, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer)
                ) {
                    Text("Listo", color = OnPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Success/Error Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (isSuccess) Color(0xFFECFDF5) else Color(0xFFFEF2F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (isSuccess) "¡Emitido con éxito!" else "Ocurrió un problema",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            if (isSuccess) {
                Text(
                    text = state.documentId ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryContainer
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFECFDF5))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ACEPTADO POR SUNAT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981)
                    )
                }
            } else {
                Text(
                    text = state.sunatResponse?.sunatDescription ?: "Error desconocido",
                    fontSize = 14.sp,
                    color = OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // Summary Card
            if (isSuccess) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ResultRow("Serie:", state.series ?: "")
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                    ResultRow("Número:", state.correlative ?: "")
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                    ResultRow("Fecha:", LocalDate.now().toString()) 
                    
                    // Hash section
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Text("Hash CDR:", fontSize = 14.sp, color = OnSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceContainerHigh)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "a3f8c2b5d4e6f7a8b9c0d1e2f3a4b5c6", // Mock hash
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Outline,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { /* Copy hash */ }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Share Section
                Text(
                    text = "COMPARTIR COMPROBANTE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Outline,
                    letterSpacing = 1.sp
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShareButton(Icons.Default.PictureAsPdf, "PDF") { /* Share PDF */ }
                    Spacer(Modifier.width(24.dp))
                    ShareButton(Icons.Default.Mail, "Correo") { /* Share Mail */ }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 14.sp, color = OnSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OnSurface)
    }
}

@Composable
private fun ShareButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerHigh)
                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = OnSurfaceVariant)
        }
        Text(text = label, fontSize = 12.sp, color = OnSurfaceVariant)
    }
}
