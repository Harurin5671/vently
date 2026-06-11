package com.app.crowns.settings.ui

import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.settings.ui.components.BusinessProfileCard
import com.app.crowns.settings.ui.components.SettingsItem
import com.app.crowns.settings.ui.components.SettingsItemWithBadge
import com.app.crowns.settings.ui.components.SettingsSection
import com.app.crowns.settings.ui.components.SettingsShimmer
import com.app.crowns.ui.components.VentlyModalBottomSheet

// ---------------------------------------------------------------------------
// Entry point
// ---------------------------------------------------------------------------

@Composable
fun SettingsScreen(
    onNavigateToEditBusiness: () -> Unit = {},
    onNavigateToEmail: () -> Unit = {},
    onNavigateToPassword: () -> Unit = {},
    onNavigateToPlan: () -> Unit = {},
    onNavigateToHelp: () -> Unit = {},
    onNavigateToReport: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(SettingsEvent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.navigateToAuth) {
        if (state.navigateToAuth) onLogout()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                bytes?.let { b ->
                    val base64 = Base64.encodeToString(b, Base64.NO_WRAP)
                    viewModel.onEvent(SettingsEvent.UploadCertificate(base64))
                }
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    SettingsContent(
        isLoading = state.isLoading,
        isDeleting = state.isDeleting,
        companyInfo = state.companyInfo,
        userInfo = state.userInfo,
        documentSeries = state.documentSeries,
        onNavigateToEditBusiness = onNavigateToEditBusiness,
        onSelectCertificate = {
            launcher.launch("*/*")
        },
        onUpdateCertificatePassword = { password ->
            viewModel.onEvent(SettingsEvent.UpdateCertificatePassword(password))
        },
        onUpdateDocumentSeries = { boletaS, boletaC, facturaS, facturaC ->
            viewModel.onEvent(SettingsEvent.UpdateDocumentSeries(boletaS, boletaC, facturaS, facturaC))
        },
        onUpdateSolCredentials = { username, password ->
            viewModel.onEvent(SettingsEvent.UpdateSolCredentials(username, password))
        },
        onRefresh = { viewModel.onEvent(SettingsEvent.Refresh) },
        onNavigateToEmail = onNavigateToEmail,
        onNavigateToPassword = onNavigateToPassword,
        onNavigateToPlan = onNavigateToPlan,
        onNavigateToHelp = onNavigateToHelp,
        onNavigateToReport = onNavigateToReport,
        onLogout = { viewModel.onEvent(SettingsEvent.Logout) },
        onDeleteAccount = { viewModel.onEvent(SettingsEvent.DeleteAccount) },
    )
}

// ---------------------------------------------------------------------------
// Content
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    isLoading: Boolean,
    isDeleting: Boolean,
    companyInfo: CompanyInfo?,
    userInfo: com.app.crowns.domain.auth.model.User?,
    documentSeries: List<DocumentSeries>,
    onNavigateToEditBusiness: () -> Unit,
    onSelectCertificate: () -> Unit,
    onUpdateCertificatePassword: (String) -> Unit,
    onUpdateDocumentSeries: (String, Int, String, Int) -> Unit,
    onUpdateSolCredentials: (String, String) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToEmail: () -> Unit,
    onNavigateToPassword: () -> Unit,
    onNavigateToPlan: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToReport: () -> Unit,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    var pushNotifications by remember { mutableStateOf(true) }
    var lowStockAlert by remember { mutableStateOf(true) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showSeriesDialog by remember { mutableStateOf(false) }
    var showSolDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var tempPassword by remember { mutableStateOf("") }
    var tempSolUsername by remember { mutableStateOf("") }
    var tempSolPassword by remember { mutableStateOf("") }
    var tempBoletaSeries by remember { mutableStateOf("") }
    var tempBoletaCorrelative by remember { mutableStateOf("") }
    var tempFacturaSeries by remember { mutableStateOf("") }
    var tempFacturaCorrelative by remember { mutableStateOf("") }

    val boletaData = remember(documentSeries) { documentSeries.find { it.documentType == ElectronicDocumentTypeEntity.BOLETA } }
    val facturaData = remember(documentSeries) { documentSeries.find { it.documentType == ElectronicDocumentTypeEntity.FACTURA } }

    val currentBoleta = boletaData?.serie ?: "B001"
    val currentFactura = facturaData?.serie ?: "F001"
    val currentBoletaC = boletaData?.lastCorrelative ?: 0
    val currentFacturaC = facturaData?.lastCorrelative ?: 0
    
    val boletaSubtitle = "${boletaData?.serie ?: "B001"}-${(boletaData?.lastCorrelative ?: 0).toString().padStart(8, '0')}"
    val facturaSubtitle = "${facturaData?.serie ?: "F001"}-${(facturaData?.lastCorrelative ?: 0).toString().padStart(8, '0')}"

    LaunchedEffect(showSeriesDialog) {
        if (showSeriesDialog) {
            tempBoletaSeries = currentBoleta
            tempBoletaCorrelative = currentBoletaC.toString()
            tempFacturaSeries = currentFactura
            tempFacturaCorrelative = currentFacturaC.toString()
        }
    }
    val context = LocalContext.current
    val appVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (_: Exception) {
            "1.0.0"
        }
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ajustes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
            )
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isLoading && companyInfo != null,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Spacer(Modifier.height(0.dp))

                if (isLoading && companyInfo == null) {
                    SettingsShimmer()
                } else {
                    // ── Business Profile Card ──────────────────────────────────────
                    BusinessProfileCard(
                        companyInfo = companyInfo,
                        onEdit = onNavigateToEditBusiness
                    )

                // ── Facturación ───────────────────────────────────────────────
                SettingsSection(title = "Facturación") {
                    SettingsItem(
                        title = "Series y correlativos",
                        subtitle = "$boletaSubtitle · $facturaSubtitle",
                        trailingIcon = Icons.Default.ChevronRight,
                        onClick = { showSeriesDialog = true },
                        showDivider = true,
                    )
                    SettingsItemWithBadge(
                        title = "Certificado digital",
                        badgeText = if (companyInfo?.certificateBase64 != null) "ACTIVO" else "PENDIENTE",
                        badgeGreen = companyInfo?.certificateBase64 != null,
                        trailingIcon = Icons.Default.ChevronRight,
                        onClick = onSelectCertificate,
                        showDivider = true,
                    )
                    SettingsItemWithBadge(
                        title = "Contraseña del certificado",
                        badgeText = if (companyInfo?.certificatePassword != null) "ACTIVO" else "PENDIENTE",
                        badgeGreen = companyInfo?.certificatePassword != null,
                        trailingIcon = Icons.Default.ChevronRight,
                        onClick = {
                            tempPassword = companyInfo?.certificatePassword ?: ""
                            showPasswordDialog = true
                        },
                        showDivider = true,
                    )
                    SettingsItemWithBadge(
                        title = "Usuario y contraseña SOL",
                        badgeText = if (companyInfo?.solUsername != null && companyInfo.solPassword != null) "ACTIVO" else "PENDIENTE",
                        badgeGreen = companyInfo?.solUsername != null && companyInfo.solPassword != null,
                        trailingIcon = Icons.Default.ChevronRight,
                        onClick = {
                            tempSolUsername = companyInfo?.solUsername ?: ""
                            tempSolPassword = companyInfo?.solPassword ?: ""
                            showSolDialog = true
                        },
                        showDivider = false,
                    )
                }

                // ── Cuenta ────────────────────────────────────────────────────
                SettingsSection(title = "Cuenta") {
                    SettingsItem(
                        title = "Correo electrónico",
                        subtitle = userInfo?.email ?: "Cargando...",
                        trailingIcon = Icons.Default.ChevronRight,
                        onClick = onNavigateToEmail,
                        showDivider = true,
                    )
                }

                // ── Danger Zone ───────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    TextButton(onClick = onLogout) {
                        Text(
                            text = "Cerrar sesión",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Text(
                        text = "Eliminar cuenta",
                        color = OutlineVariant,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable { showDeleteAccountDialog = true }
                            .padding(4.dp),
                    )
                }
            }

                // ── Footer ────────────────────────────────────────────────────
                Text(
                    text = "Vently v$appVersion",
                    color = OutlineVariant,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
            }
        }

        if (showSeriesDialog) {
            VentlyModalBottomSheet(
                onDismiss = { showSeriesDialog = false },
                title = "Series y correlativos",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "BOLETAS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Outline,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = tempBoletaSeries,
                                onValueChange = { },
                                label = { Text("Serie") },
                                readOnly = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = OutlineVariant,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedTextField(
                                value = tempBoletaCorrelative,
                                onValueChange = { if (it.all { char -> char.isDigit() }) tempBoletaCorrelative = it },
                                label = { Text("Correlativo") },
                                placeholder = { Text("0") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = OutlineVariant,
                                ),
                                modifier = Modifier.weight(1.5f),
                            )
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "FACTURAS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Outline,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = tempFacturaSeries,
                                onValueChange = { },
                                label = { Text("Serie") },
                                readOnly = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = OutlineVariant,
                                ),
                                modifier = Modifier.weight(1f),
                            )
                            OutlinedTextField(
                                value = tempFacturaCorrelative,
                                onValueChange = { if (it.all { char -> char.isDigit() }) tempFacturaCorrelative = it },
                                label = { Text("Correlativo") },
                                placeholder = { Text("0") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = OutlineVariant,
                                ),
                                modifier = Modifier.weight(1.5f),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        onUpdateDocumentSeries(
                            tempBoletaSeries,
                            tempBoletaCorrelative.toIntOrNull() ?: 0,
                            tempFacturaSeries,
                            tempFacturaCorrelative.toIntOrNull() ?: 0,
                        )
                        showSeriesDialog = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text("Guardar", color = OnPrimary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        if (showPasswordDialog) {
            var passwordVisible by remember { mutableStateOf(false) }
            VentlyModalBottomSheet(
                onDismiss = { showPasswordDialog = false },
                title = "Contraseña del certificado",
            ) {
                OutlinedTextField(
                    value = tempPassword,
                    onValueChange = { tempPassword = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = OnSurfaceVariant,
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = OutlineVariant,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        onUpdateCertificatePassword(tempPassword)
                        showPasswordDialog = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text("Guardar", color = OnPrimary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        if (showDeleteAccountDialog) {
            VentlyModalBottomSheet(
                onDismiss = { showDeleteAccountDialog = false },
                title = "Eliminar cuenta",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Esta acción es permanente e irreversible.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Se eliminarán permanentemente:",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            "Tu cuenta y datos de acceso",
                            "Información de tu empresa",
                            "Historial de boletas y facturas",
                            "Productos e inventario",
                        ).forEach { item ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "•",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 16.sp,
                                )
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = OnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    },
                    enabled = !isDeleting,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        text = if (isDeleting) "Eliminando cuenta..." else "Sí, eliminar mi cuenta",
                        color = OnPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = { showDeleteAccountDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Cancelar",
                        color = OnSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        if (showSolDialog) {
            var solPasswordVisible by remember { mutableStateOf(false) }
            VentlyModalBottomSheet(
                onDismiss = { showSolDialog = false },
                title = "Credenciales SOL",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tempSolUsername,
                        onValueChange = { tempSolUsername = it },
                        label = { Text("Usuario SOL") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = tempSolPassword,
                        onValueChange = { tempSolPassword = it },
                        label = { Text("Contraseña SOL") },
                        singleLine = true,
                        visualTransformation = if (solPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { solPasswordVisible = !solPasswordVisible }) {
                                Icon(
                                    imageVector = if (solPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant,
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        onUpdateSolCredentials(tempSolUsername, tempSolPassword)
                        showSolDialog = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text("Guardar", color = OnPrimary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}