package com.app.crowns.settings.ui

import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.settings.ui.components.*

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
        companyInfo = state.companyInfo,
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
        onNavigateToEmail = onNavigateToEmail,
        onNavigateToPassword = onNavigateToPassword,
        onNavigateToPlan = onNavigateToPlan,
        onNavigateToHelp = onNavigateToHelp,
        onNavigateToReport = onNavigateToReport,
        onLogout = onLogout,
        onDeleteAccount = onDeleteAccount,
    )
}

// ---------------------------------------------------------------------------
// Content
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    companyInfo: CompanyInfo?,
    documentSeries: List<DocumentSeries>,
    onNavigateToEditBusiness: () -> Unit,
    onSelectCertificate: () -> Unit,
    onUpdateCertificatePassword: (String) -> Unit,
    onUpdateDocumentSeries: (String, Int, String, Int) -> Unit,
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
    var tempPassword by remember { mutableStateOf("") }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Spacer(Modifier.height(0.dp))

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
                    showDivider = false,
                )
            }

            if (showSeriesDialog) {
                AlertDialog(
                    onDismissRequest = { showSeriesDialog = false },
                    title = { 
                        Text(
                            text = "Series y correlativos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            // Section Boleta
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "BOLETAS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Outline
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = tempBoletaSeries,
                                        onValueChange = { },
                                        label = { Text("Serie") },
                                        readOnly = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = tempBoletaCorrelative,
                                        onValueChange = { if (it.all { char -> char.isDigit() }) tempBoletaCorrelative = it },
                                        label = { Text("Correlativo") },
                                        placeholder = { Text("0") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1.5f)
                                    )
                                }
                            }

                            // Section Factura
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "FACTURAS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Outline
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = tempFacturaSeries,
                                        onValueChange = { },
                                        label = { Text("Serie") },
                                        readOnly = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = tempFacturaCorrelative,
                                        onValueChange = { if (it.all { char -> char.isDigit() }) tempFacturaCorrelative = it },
                                        label = { Text("Correlativo") },
                                        placeholder = { Text("0") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1.5f)
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onUpdateDocumentSeries(
                                    tempBoletaSeries,
                                    tempBoletaCorrelative.toIntOrNull() ?: 0,
                                    tempFacturaSeries,
                                    tempFacturaCorrelative.toIntOrNull() ?: 0
                                )
                                showSeriesDialog = false
                            }
                        ) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSeriesDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            if (showPasswordDialog) {
                AlertDialog(
                    onDismissRequest = { showPasswordDialog = false },
                    title = { Text("Contraseña del certificado") },
                    text = {
                        OutlinedTextField(
                            value = tempPassword,
                            onValueChange = { tempPassword = it },
                            label = { Text("Contraseña") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onUpdateCertificatePassword(tempPassword)
                                showPasswordDialog = false
                            }
                        ) {
                            Text("Guardar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPasswordDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            // ── Cuenta ────────────────────────────────────────────────────
            SettingsSection(title = "Cuenta") {
                SettingsItem(
                    title = "Correo electrónico",
                    subtitle = "fere******@gmail.com", // TODO: Get real email
                    trailingIcon = Icons.Default.ChevronRight,
                    onClick = onNavigateToEmail,
                    showDivider = true,
                )
//                SettingsItem(
//                    title = "Cambiar contraseña",
//                    trailingIcon = Icons.Default.Lock,
//                    onClick = onNavigateToPassword,
//                    showDivider = true,
//                )
//                SettingsItemWithBadge(
//                    title = "Plan actual",
//                    badgeText = "GRATUITO",
//                    badgeGreen = false,
//                    trailingIcon = Icons.Default.ChevronRight,
//                    onClick = onNavigateToPlan,
//                    showDivider = false,
//                )
            }

            // ── Preferencias ──────────────────────────────────────────────
//            SettingsSection(title = "Preferencias") {
//                SettingsToggleItem(
//                    title = "Notificaciones push",
//                    checked = pushNotifications,
//                    onCheckedChange = { pushNotifications = it },
//                    showDivider = true,
//                )
//                SettingsToggleItem(
//                    title = "Alerta de stock bajo",
//                    checked = lowStockAlert,
//                    onCheckedChange = { lowStockAlert = it },
//                    showDivider = false,
//                )
//            }

            // ── Soporte ───────────────────────────────────────────────────
//            SettingsSection(title = "Soporte") {
//                SettingsItem(
//                    title = "Centro de ayuda",
//                    trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
//                    onClick = onNavigateToHelp,
//                    showDivider = true,
//                )
//                SettingsItem(
//                    title = "Reportar un problema",
//                    trailingIcon = Icons.Default.Flag,
//                    onClick = onNavigateToReport,
//                    showDivider = false,
//                )
//            }

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
                        .clickable { onDeleteAccount() }
                        .padding(4.dp),
                )
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
}