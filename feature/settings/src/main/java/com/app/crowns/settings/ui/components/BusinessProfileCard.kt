package com.app.crowns.settings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.company.model.CompanyInfo

@Composable
fun BusinessProfileCard(
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
                    textColor = Success,
                    backgroundColor = SuccessBg,
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