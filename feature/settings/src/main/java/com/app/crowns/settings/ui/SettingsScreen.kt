package com.app.crowns.settings.ui

import android.provider.OpenableColumns
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.settings.ui.components.BusinessProfileCard
import com.app.crowns.settings.ui.components.SettingsItem
import com.app.crowns.settings.ui.components.SettingsItemWithBadge
import com.app.crowns.settings.ui.components.SettingsSection
import com.app.crowns.settings.ui.components.SettingsShimmer
import com.app.crowns.settings.ui.components.dialogs.CertPasswordDialog
import com.app.crowns.settings.ui.components.dialogs.DeleteAccountDialog
import com.app.crowns.settings.ui.components.dialogs.SeriesDialog
import com.app.crowns.settings.ui.components.dialogs.SolDialog
import com.app.crowns.ui.components.VentlySnackbarHost
import com.app.crowns.ui.components.VentlySnackbarState
import com.app.crowns.ui.components.VentlySnackbarType
import com.app.crowns.ui.components.rememberVentlySnackbarState
import kotlinx.coroutines.launch

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
    val snackbarState = rememberVentlySnackbarState()

    LaunchedEffect(state.error) {
        val error = state.error
        if (error != null) {
            snackbarState.show(error)
            viewModel.onEvent(SettingsEvent.DismissError)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(SettingsEvent.SilentRefresh)
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
                val fileName = context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (idx != -1) cursor.getString(idx) else null
                    } else null
                } ?: ""

                val ext = fileName.substringAfterLast('.', "").lowercase()
                if (ext != "p12" && ext != "pfx") {
                    viewModel.onEvent(SettingsEvent.SetError("Formato no válido. El certificado debe ser un archivo .p12 o .pfx"))
                    return@rememberLauncherForActivityResult
                }

                val inputStream = context.contentResolver.openInputStream(it)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes == null || bytes.size < 4) {
                    viewModel.onEvent(SettingsEvent.SetError("El archivo está vacío o dañado"))
                    return@rememberLauncherForActivityResult
                }

                // Verificar firma PKCS#12: ASN.1 SEQUENCE (0x30) + long-form length (0x82)
                if (bytes[0] != 0x30.toByte() || bytes[1] != 0x82.toByte()) {
                    viewModel.onEvent(SettingsEvent.SetError("El archivo no es un certificado .p12/.pfx válido"))
                    return@rememberLauncherForActivityResult
                }

                val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                viewModel.onEvent(SettingsEvent.UploadCertificate(base64))
            } catch (e: Exception) {
                viewModel.onEvent(SettingsEvent.SetError("No se pudo leer el archivo del certificado"))
            }
        }
    }

    SettingsContent(
        isLoading = state.isLoading,
        isDeleting = state.isDeleting,
        companyInfo = state.companyInfo,
        userInfo = state.userInfo,
        documentSeries = state.documentSeries,
        isSubscriptionActive = state.isSubscriptionActive,
        snackbarState = snackbarState,
        onNavigateToEditBusiness = onNavigateToEditBusiness,
        onSelectCertificate = { launcher.launch("*/*") },
        onUpdateCertificatePassword = { viewModel.onEvent(SettingsEvent.UpdateCertificatePassword(it)) },
        onUpdateDocumentSeries = { bs, bc, fs, fc ->
            viewModel.onEvent(SettingsEvent.UpdateDocumentSeries(bs, bc, fs, fc))
        },
        onUpdateSolCredentials = { u, p -> viewModel.onEvent(SettingsEvent.UpdateSolCredentials(u, p)) },
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
    isSubscriptionActive: Boolean?,
    snackbarState: VentlySnackbarState,
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
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showSeriesDialog by remember { mutableStateOf(false) }
    var showSolDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val boletaData = remember(documentSeries) { documentSeries.find { it.documentType == ElectronicDocumentTypeEntity.BOLETA } }
    val facturaData = remember(documentSeries) { documentSeries.find { it.documentType == ElectronicDocumentTypeEntity.FACTURA } }

    val currentBoleta = boletaData?.serie ?: "B001"
    val currentFactura = facturaData?.serie ?: "F001"
    val currentBoletaC = boletaData?.lastCorrelative ?: 0
    val currentFacturaC = facturaData?.lastCorrelative ?: 0

    val boletaSubtitle = "${boletaData?.serie ?: "B001"}-${(boletaData?.lastCorrelative ?: 0).toString().padStart(8, '0')}"
    val facturaSubtitle = "${facturaData?.serie ?: "F001"}-${(facturaData?.lastCorrelative ?: 0).toString().padStart(8, '0')}"

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
        snackbarHost = { VentlySnackbarHost(snackbarState) },
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
                    BusinessProfileCard(companyInfo = companyInfo, onEdit = onNavigateToEditBusiness)

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
                            onClick = { showPasswordDialog = true },
                            showDivider = true,
                        )
                        SettingsItemWithBadge(
                            title = "Usuario y contraseña SOL",
                            badgeText = if (companyInfo?.solUsername != null && companyInfo.solPassword != null) "ACTIVO" else "PENDIENTE",
                            badgeGreen = companyInfo?.solUsername != null && companyInfo.solPassword != null,
                            trailingIcon = Icons.Default.ChevronRight,
                            onClick = { showSolDialog = true },
                            showDivider = false,
                        )
                    }

                    SettingsSection(title = "Cuenta") {
                        SettingsItemWithBadge(
                            title = "Suscripción",
                            badgeText = when (isSubscriptionActive) {
                                true -> "ACTIVA"
                                false -> "PENDIENTE"
                                null -> "..."
                            },
                            badgeGreen = isSubscriptionActive == true,
                            trailingIcon = null,
                            onClick = {},
                            showDivider = true,
                        )
                        SettingsItem(
                            title = "Correo electrónico",
                            subtitle = userInfo?.email ?: "Cargando...",
                            trailingIcon = Icons.Default.ChevronRight,
                            onClick = onNavigateToEmail,
                            showDivider = true,
                        )
                    }

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

            if (showSeriesDialog) {
                SeriesDialog(
                    initialBoletaSeries = currentBoleta,
                    initialBoletaCorrelative = currentBoletaC,
                    initialFacturaSeries = currentFactura,
                    initialFacturaCorrelative = currentFacturaC,
                    onSave = { bs, bc, fs, fc ->
                        onUpdateDocumentSeries(bs, bc, fs, fc)
                        showSeriesDialog = false
                        scope.launch {
                            snackbarState.show("Guardado correctamente", VentlySnackbarType.SUCCESS)
                        }
                    },
                    onDismiss = { showSeriesDialog = false },
                )
            }

            if (showPasswordDialog) {
                CertPasswordDialog(
                    initialPassword = companyInfo?.certificatePassword ?: "",
                    onSave = { password ->
                        onUpdateCertificatePassword(password)
                        showPasswordDialog = false
                        scope.launch {
                            snackbarState.show("Guardado correctamente", VentlySnackbarType.SUCCESS)
                        }
                    },
                    onDismiss = { showPasswordDialog = false },
                )
            }

            if (showDeleteAccountDialog) {
                DeleteAccountDialog(
                    isDeleting = isDeleting,
                    onConfirm = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    },
                    onDismiss = { showDeleteAccountDialog = false },
                )
            }

            if (showSolDialog) {
                SolDialog(
                    initialUsername = companyInfo?.solUsername ?: "",
                    initialPassword = companyInfo?.solPassword ?: "",
                    onSave = { u, p ->
                        onUpdateSolCredentials(u, p)
                        showSolDialog = false
                        scope.launch {
                            snackbarState.show("Guardado correctamente", VentlySnackbarType.SUCCESS)
                        }
                    },
                    onDismiss = { showSolDialog = false },
                )
            }
        }
    }
}
