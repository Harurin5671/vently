package com.app.crowns.invoice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnPrimary
import com.app.crowns.designsystem.color.OnSurfaceVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.SurfaceContainerHigh
import com.app.crowns.invoice.domain.model.InvoiceType

@Composable
fun InvoiceTypeSelector(
    selected: InvoiceType,
    onSelect: (InvoiceType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerHigh)
            .padding(4.dp),
    ) {
        InvoiceType.entries.forEach { type ->
            val isSelected = selected == type
            Text(
                text = type.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) OnPrimary else OnSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Primary else Color.Transparent)
                    .clickable { onSelect(type) }
                    .padding(vertical = 10.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}