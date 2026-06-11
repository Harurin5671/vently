package com.app.crowns.stock.ui.addproduct.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.catalog.entities.IgvTypeEntity
import com.app.crowns.ui.components.VentlyModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IgvTypeSelector(
    selected: IgvTypeEntity?,
    types: List<IgvTypeEntity>,
    onSelect: (IgvTypeEntity) -> Unit,
    isError: Boolean = false,
) {
    var showSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredTypes = remember(searchQuery, types) {
        if (searchQuery.isBlank()) types
        else types.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column {
        Text(
            text = "Tipo de IGV *",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isError) ErrorBright else OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = selected?.name ?: "Seleccionar tipo",
            onValueChange = {},
            readOnly = true,
            isError = isError,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isError) ErrorBright else OnSurfaceVariant,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSheet = true },
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = if (isError) ErrorBright else OutlineVariant,
                disabledTextColor = OnSurface,
                disabledTrailingIconColor = if (isError) ErrorBright else OnSurfaceVariant,
            ),
        )
    }

    if (showSheet) {
        VentlyModalBottomSheet(
            onDismiss = {
                showSheet = false
                searchQuery = ""
            },
            title = "Tipo de IGV",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            searchPlaceholder = "Buscar tipo de IGV...",
        ) {
            LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                items(filteredTypes) { type ->
                    val isSelected = type.code == selected?.code
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(type)
                                showSheet = false
                            }
                            .background(
                                if (isSelected) PrimaryContainer.copy(alpha = 0.15f) else Color.Transparent
                            )
                            .padding(horizontal = 4.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = type.name,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Primary else OnSurface,
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isSelected) Primary.copy(alpha = 0.12f)
                                        else OutlineVariant.copy(alpha = 0.4f)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            ) {
                                Text(
                                    text = "${type.percentage}% de IGV",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Primary else OnSurfaceVariant,
                                )
                            }
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.4f))
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}