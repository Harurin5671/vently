package com.app.crowns.invoice.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
import com.app.crowns.invoice.ui.components.ResultRow
import com.app.crowns.invoice.ui.components.ShareButton
import com.app.crowns.invoice.ui.preview.InvoicePreviewUiState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceResultScreen(
    state: InvoicePreviewUiState,
    onClose: () -> Unit,
    onNavigateToHistory: (invoiceId: String?) -> Unit,
    onFinish: () -> Unit,
    onDownloadPdf: () -> Unit,
    onShare: () -> Unit,
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
                    onClick = { onNavigateToHistory(state.invoiceId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, PrimaryContainer)
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
                    .background(if (isSuccess) SuccessBg else StockNoneBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isSuccess) Success else ErrorBright,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (isSuccess) "¡Emitido con éxito!" else "Ocurrió un problema",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextStrong
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
                        .background(SuccessBg)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ACEPTADO POR SUNAT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Success
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
                        .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ResultRow("Serie:", state.series ?: "")
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                    ResultRow("Número:", state.correlative ?: "")
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.3f))
                    ResultRow("Fecha:", LocalDate.now().toString())
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
                    ShareButton(Icons.Default.PictureAsPdf, "PDF") { onDownloadPdf() }
                    Spacer(Modifier.width(24.dp))
                    ShareButton(Icons.Default.Share, "Compartir") { onShare() }
                }
            }
        }
    }
}