package com.app.crowns.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.company.model.CompanyInfo

// ---------------------------------------------------------------------------
// Entry point
// ---------------------------------------------------------------------------

@Composable
fun SettingsScreen(
    onNavigateToEditBusiness: () -> Unit = {},
    onNavigateToSeries: () -> Unit = {},
    onNavigateToCertificate: () -> Unit = {},
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

    SettingsContent(
        companyInfo = state.companyInfo,
        onNavigateToEditBusiness = onNavigateToEditBusiness,
        onNavigateToSeries = onNavigateToSeries,
        onNavigateToCertificate = onNavigateToCertificate,
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
    onNavigateToEditBusiness: () -> Unit,
    onNavigateToSeries: () -> Unit,
    onNavigateToCertificate: () -> Unit,
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
                    subtitle = "B001 · F001",
                    trailingIcon = Icons.Default.ChevronRight,
                    onClick = onNavigateToSeries,
                    showDivider = true,
                )
                SettingsItemWithBadge(
                    title = "Certificado digital",
                    badgeText = "ACTIVO",
                    badgeGreen = true,
                    trailingIcon = Icons.Default.ChevronRight,
                    onClick = onNavigateToCertificate,
                    showDivider = false,
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
                SettingsItem(
                    title = "Cambiar contraseña",
                    trailingIcon = Icons.Default.Lock,
                    onClick = onNavigateToPassword,
                    showDivider = true,
                )
                SettingsItemWithBadge(
                    title = "Plan actual",
                    badgeText = "GRATUITO",
                    badgeGreen = false,
                    trailingIcon = Icons.Default.ChevronRight,
                    onClick = onNavigateToPlan,
                    showDivider = false,
                )
            }

            // ── Preferencias ──────────────────────────────────────────────
            SettingsSection(title = "Preferencias") {
                SettingsToggleItem(
                    title = "Notificaciones push",
                    checked = pushNotifications,
                    onCheckedChange = { pushNotifications = it },
                    showDivider = true,
                )
                SettingsToggleItem(
                    title = "Alerta de stock bajo",
                    checked = lowStockAlert,
                    onCheckedChange = { lowStockAlert = it },
                    showDivider = false,
                )
            }

            // ── Soporte ───────────────────────────────────────────────────
            SettingsSection(title = "Soporte") {
                SettingsItem(
                    title = "Centro de ayuda",
                    trailingIcon = Icons.AutoMirrored.Filled.OpenInNew,
                    onClick = onNavigateToHelp,
                    showDivider = true,
                )
                SettingsItem(
                    title = "Reportar un problema",
                    trailingIcon = Icons.Default.Flag,
                    onClick = onNavigateToReport,
                    showDivider = false,
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

// ---------------------------------------------------------------------------
// Business Profile Card
// ---------------------------------------------------------------------------

@Composable
private fun BusinessProfileCard(
    companyInfo: CompanyInfo?,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Avatar con logo o iniciales
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(PrimaryContainer.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            if (companyInfo?.logoUrl != null) {
                AsyncImage(
                    model = companyInfo.logoUrl,
                    contentDescription = "Logo de la empresa",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                val initials = companyInfo?.businessName
                    ?.split(" ")
                    ?.filter { it.isNotBlank() }
                    ?.take(2)
                    ?.joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
                    ?.ifEmpty { "V" } ?: "V"

                Text(
                    text = initials,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary,
                )
            }
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = companyInfo?.businessName ?: "Cargando...",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                StatusBadge(
                    text = "ACTIVO · SUNAT",
                    textColor = Color(0xFF10B981),
                    backgroundColor = Color(0xFFECFDF5),
                )
            }
            Text(
                text = companyInfo?.let { "RUC ${it.documentNumber}" } ?: "...",
                fontSize = 13.sp,
                color = OnSurfaceVariant,
            )
        }

        TextButton(onClick = onEdit) {
            Text(
                text = "Editar",
                color = Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Section wrapper
// ---------------------------------------------------------------------------

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = OutlineVariant,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 4.dp),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLowest)
                .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp)),
            content = content,
        )
    }
}

// ---------------------------------------------------------------------------
// Item variants
// ---------------------------------------------------------------------------

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String? = null,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 13.sp,
                        color = OnSurfaceVariant,
                    )
                }
            }
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = Outline,
                modifier = Modifier.size(20.dp),
            )
        }
        if (showDivider) {
            HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
        }
    }
}

@Composable
private fun SettingsItemWithBadge(
    title: String,
    badgeText: String,
    badgeGreen: Boolean,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    showDivider: Boolean,
) {
    val badgeTextColor = if (badgeGreen) Color(0xFF10B981)
    else
        Primary

    val badgeBgColor = if (badgeGreen) Color(0xFFECFDF5)
    else
        PrimaryContainer.copy(alpha = 0.15f)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
                modifier = Modifier.weight(1f),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeTextColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(badgeBgColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                )
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = Outline,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
        }
    }
}

@Composable
private fun SettingsToggleItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
                modifier = Modifier.weight(1f),
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = OnPrimary,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = OnSurface,
                    uncheckedTrackColor = OutlineVariant,
                ),
            )
        }
        if (showDivider) {
            HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    textColor: Color,
    backgroundColor: Color,
) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = textColor,
        maxLines = 1,
        softWrap = false,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}