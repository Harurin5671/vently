package com.app.crowns.home.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.app.crowns.designsystem.color.OnSurfaceVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.Surface
import com.app.crowns.domain.company.model.CompanyInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopAppBaR(
    companyInfo: CompanyInfo?,
    userName: String,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = companyInfo?.businessName ?: "Vently",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Primary,
                maxLines = 1,
            )
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificaciones",
                    tint = OnSurfaceVariant,
                )
            }

            if (companyInfo?.logoUrl != null) {
                AsyncImage(
                    model = companyInfo.logoUrl,
                    contentDescription = "Logo de la empresa",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer.copy(alpha = 0.1f)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Avatar con iniciales
                val initials = userName
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.firstOrNull()?.uppercase() ?: "" }
                    .ifEmpty { "U" }

                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryContainer.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initials,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Surface,
        ),
    )
}