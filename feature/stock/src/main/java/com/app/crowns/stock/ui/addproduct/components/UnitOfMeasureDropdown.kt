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
import com.app.crowns.domain.catalog.entities.UnitOfMeasureEntity
import com.app.crowns.ui.components.VentlyModalBottomSheet
import kotlin.text.contains

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitOfMeasureDropdown(
    selected: String,
    units: List<UnitOfMeasureEntity>,
    onSelect: (String) -> Unit,
    isError: Boolean = false,
) {
    var showSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val selectedUnit = units.find { it.code == selected }
    val selectedLabel = selectedUnit?.let { "${it.code} - ${it.name}" } ?: "Seleccionar unidad"
    val filteredUnits = remember(searchQuery, units) {
        if (searchQuery.isBlank()) units
        else units.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.code.contains(searchQuery, ignoreCase = true)
        }
    }

    Column {
        Text(
            text = "Unidad de medida *",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isError) ErrorBright else OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = selectedLabel,
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
            title = "Unidad de medida",
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            searchPlaceholder = "Buscar por nombre o código...",
        ) {
            LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                items(filteredUnits) { unit ->
                    val isSelected = unit.code == selected
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(unit.code)
                                showSheet = false
                            }
                            .background(
                                if (isSelected) Primary.copy(alpha = 0.08f) else Color.Transparent
                            )
                            .padding(horizontal = 4.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = unit.name,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Primary else OnSurface,
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Primary.copy(alpha = 0.10f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = unit.code,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Primary,
                                    )
                                }
                                if (unit.description.isNotBlank()) {
                                    Text(
                                        text = unit.description,
                                        fontSize = 12.sp,
                                        color = OnSurfaceVariant,
                                    )
                                }
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
                    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.5f))
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}