package com.app.crowns.settings.ui.business

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.crowns.designsystem.color.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBusinessProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditBusinessProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            bytes?.let { b -> viewModel.onLogoSelected(b) }
        }
    }

    EditBusinessProfileContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onLogoClick = { launcher.launch("image/*") },
        onSave = viewModel::onSave
    )

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
            viewModel.resetSavedState()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBusinessProfileContent(
    state: EditBusinessProfileUiState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onLogoClick: () -> Unit,
    onSave: () -> Unit
) {
    var showLogoFullscreen by remember { mutableStateOf(false) }
    val hasLogo = state.logoUrl != null || state.selectedLogoBytes != null

    if (showLogoFullscreen && hasLogo) {
        Dialog(
            onDismissRequest = { showLogoFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f))
                    .clickable { showLogoFullscreen = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = state.selectedLogoBytes ?: state.logoUrl,
                    contentDescription = "Logo ampliado",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { showLogoFullscreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = OnPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }

    Scaffold(
        containerColor = Surface,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi negocio",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = OnSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onSave, enabled = !state.isSaving) {
                        Text(
                            text = if (state.isSaving) "Guardando..." else "Guardar",
                            color = Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
                modifier = Modifier.border(0.5.dp, OutlineVariant)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // 1. LOGO SECTION
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LOGO DEL NEGOCIO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    modifier = Modifier.fillMaxWidth(),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainerLowest)
                            .border(2.dp, PrimaryFixed, CircleShape)
                            .then(
                                if (hasLogo) Modifier.clickable { showLogoFullscreen = true }
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.logoUrl != null || state.selectedLogoBytes != null) {
                            AsyncImage(
                                model = state.selectedLogoBytes ?: state.logoUrl,
                                contentDescription = "Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("V", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Primary)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainer)
                            .border(2.dp, Surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onLogoClick, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar logo",
                                modifier = Modifier.size(14.dp),
                                tint = OnPrimary
                            )
                        }
                    }
                }
                TextButton(onClick = onLogoClick) {
                    Text(
                        text = "Cambiar logo",
                        color = Primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 2. BUSINESS DATA SECTION
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "DATOS GENERALES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLowest, RoundedCornerShape(12.dp))
                        .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nombre comercial (solo lectura — dato de SUNAT)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Nombre comercial",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                        ReadOnlyField(value = state.companyInfo?.businessName ?: "—")
                    }

                    // RUC (Read-only)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "RUC",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(SurfaceDim.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = state.companyInfo?.documentNumber ?: "...",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Dirección legal (solo lectura — dato de SUNAT)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Dirección legal",
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                        ReadOnlyField(value = state.companyInfo?.address ?: "—")
                    }
                }
            }

            // UX Enhancement Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryFixed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = PrimaryContainer
                )
                Text(
                    text = "Los cambios en los datos generales pueden tardar hasta 24 horas en reflejarse en tus facturas emitidas.",
                    fontSize = 13.sp,
                    color = OnSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

            // Save Button
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer),
                enabled = !state.isSaving
            ) {
                Text(
                    text = if (state.isSaving) "Guardando cambios..." else "Guardar cambios",
                    color = OnPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ReadOnlyField(value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .background(SurfaceDim.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .border(1.dp, OutlineVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = value,
            color = OnSurfaceVariant,
            fontSize = 14.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditBusinessProfileScreenPreview() {
    EditBusinessProfileContent(
        state = EditBusinessProfileUiState(
            companyInfo = null
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onNavigateBack = {},
        onLogoClick = {},
        onSave = {}
    )
}
