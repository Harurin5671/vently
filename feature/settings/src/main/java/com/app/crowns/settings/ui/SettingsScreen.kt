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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        val error = state.error
        if (error != null) {
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(SettingsEvent.DismissError)
        }
    }

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
        snackbarHostState = snackbarHostState,
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
    snackbarHostState: SnackbarHostState,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    },
                    onDismiss = { showSolDialog = false },
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Dialog composables
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeriesDialog(
    initialBoletaSeries: String,
    initialBoletaCorrelative: Int,
    initialFacturaSeries: String,
    initialFacturaCorrelative: Int,
    onSave: (boletaSeries: String, boletaCorrelative: Int, facturaSeries: String, facturaCorrelative: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var boletaSeries by remember { mutableStateOf(initialBoletaSeries) }
    var boletaCorrelative by remember { mutableStateOf(initialBoletaCorrelative.toString()) }
    var facturaSeries by remember { mutableStateOf(initialFacturaSeries) }
    var facturaCorrelative by remember { mutableStateOf(initialFacturaCorrelative.toString()) }

    VentlyModalBottomSheet(onDismiss = onDismiss, title = "Series y correlativos") {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("BOLETAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Outline)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = boletaSeries,
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
                        value = boletaCorrelative,
                        onValueChange = { if (it.all { c -> c.isDigit() }) boletaCorrelative = it },
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
                Text("FACTURAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Outline)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = facturaSeries,
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
                        value = facturaCorrelative,
                        onValueChange = { if (it.all { c -> c.isDigit() }) facturaCorrelative = it },
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
                onSave(
                    boletaSeries,
                    boletaCorrelative.toIntOrNull() ?: 0,
                    facturaSeries,
                    facturaCorrelative.toIntOrNull() ?: 0,
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CertPasswordDialog(
    initialPassword: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var password by remember { mutableStateOf(initialPassword) }
    var passwordVisible by remember { mutableStateOf(false) }

    VentlyModalBottomSheet(onDismiss = onDismiss, title = "Contraseña del certificado") {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
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
            onClick = { onSave(password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text("Guardar", color = OnPrimary, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteAccountDialog(
    isDeleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    VentlyModalBottomSheet(onDismiss = onDismiss, title = "Eliminar cuenta") {
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
                        Text("•", color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                        Text(item, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onConfirm,
            enabled = !isDeleting,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = if (isDeleting) "Eliminando cuenta..." else "Sí, eliminar mi cuenta",
                color = OnPrimary,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(4.dp))
        TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar", color = OnSurfaceVariant, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SolDialog(
    initialUsername: String,
    initialPassword: String,
    onSave: (username: String, password: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var username by remember { mutableStateOf(initialUsername) }
    var password by remember { mutableStateOf(initialPassword) }
    var passwordVisible by remember { mutableStateOf(false) }

    VentlyModalBottomSheet(onDismiss = onDismiss, title = "Credenciales SOL") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario SOL") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = OutlineVariant,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña SOL") },
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
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onSave(username, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text("Guardar", color = OnPrimary, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
    }
}
