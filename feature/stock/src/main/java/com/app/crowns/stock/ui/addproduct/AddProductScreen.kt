package com.app.crowns.stock.ui.addproduct

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.OnSurfaceVariant
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.SecondaryContainer
import com.app.crowns.designsystem.color.Surface
import com.app.crowns.designsystem.color.SurfaceContainerLowest
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.IgvType
import androidx.compose.ui.platform.LocalLocale
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun AddProductScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddProductViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AddProductNavigationEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    AddProductContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductContent(
    state: AddProductUiState,
    onEvent: (AddProductEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nuevo producto",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = OnSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface),
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(16.dp),
            ) {
                Button(
                    onClick = { onEvent(AddProductEvent.OnSaveProduct) },
                    enabled = state.isFormValid && !state.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                    ),
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Guardar producto",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(4.dp))

            // Basic info
            SectionCard(title = "INFORMACIÓN BÁSICA") {
                VentlyTextField(
                    label = "Nombre del producto *",
                    value = state.name,
                    onValueChange = { onEvent(AddProductEvent.OnNameChanged(it)) },
                    placeholder = "Ej. Zapatillas Urban Pro",
                )
                VentlyTextField(
                    label = "Descripción",
                    value = state.description,
                    onValueChange = { onEvent(AddProductEvent.OnDescriptionChanged(it)) },
                    placeholder = "Describe brevemente el producto...",
                    minLines = 3,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    VentlyTextField(
                        label = "SKU",
                        value = state.sku,
                        onValueChange = { onEvent(AddProductEvent.OnSkuChanged(it)) },
                        placeholder = "SKU-001",
                        modifier = Modifier.weight(1f),
                    )
                    CategoryDropdown(
                        categories = state.categories,
                        selected = state.selectedCategory,
                        onSelect = { onEvent(AddProductEvent.OnCategorySelected(it)) },
                        onAddNew = { onEvent(AddProductEvent.OnShowAddCategoryDialog) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Pricing
            SectionCard(title = "PRECIOS") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PriceTextField(
                        label = "Precio de venta *",
                        value = state.salePrice,
                        onValueChange = { onEvent(AddProductEvent.OnSalePriceChanged(it)) },
                        modifier = Modifier.weight(1f),
                    )
                    PriceTextField(
                        label = "Precio de costo",
                        value = state.costPrice,
                        onValueChange = { onEvent(AddProductEvent.OnCostPriceChanged(it)) },
                        modifier = Modifier.weight(1f),
                    )
                }
                // Margin
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecondaryContainer.copy(alpha = 0.1f))
                        .border(1.dp, SecondaryContainer.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Margen estimado",
                        fontSize = 14.sp,
                        color = OnSurfaceVariant,
                    )
                    Text(
                        text = String.format(LocalLocale.current.platformLocale, "%.2f%%", state.profitMargin),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                    )
                }
            }

            // SUNAT
            SectionCard(
                title = "CONFIGURACIÓN SUNAT",
                accentColor = Color(0xFFF59E0B),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalance,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "CUMPLIMIENTO FISCAL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF59E0B),
                        letterSpacing = 0.8.sp,
                    )
                }

                UnitOfMeasureDropdown(
                    selected = state.unitOfMeasure,
                    onSelect = { onEvent(AddProductEvent.OnUnitOfMeasureSelected(it)) },
                )

                IgvTypeSelector(
                    selected = state.igvType,
                    onSelect = { onEvent(AddProductEvent.OnIgvTypeSelected(it)) },
                )
            }

            // Stock control
            SectionCard(title = "CONTROL DE STOCK") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(PrimaryContainer.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Inventory2,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Column {
                            Text(
                                text = "Controlar inventario",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = OnSurface,
                            )
                            Text(
                                text = "Seguimiento automático de stock",
                                fontSize = 12.sp,
                                color = Outline,
                            )
                        }
                    }
                    Switch(
                        checked = state.trackStock,
                        onCheckedChange = { onEvent(AddProductEvent.OnTrackStockChanged(it)) },
                        colors = SwitchDefaults.colors(checkedTrackColor = Primary),
                    )
                }

                if (state.trackStock) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StockStepper(
                            label = "Stock actual",
                            value = state.currentStock,
                            onDecrement = {
                                if (state.currentStock > 0)
                                    onEvent(AddProductEvent.OnCurrentStockChanged(state.currentStock - 1))
                            },
                            onIncrement = {
                                onEvent(AddProductEvent.OnCurrentStockChanged(state.currentStock + 1))
                            },
                            modifier = Modifier.weight(1f),
                        )
                        VentlyTextField(
                            label = "Alerta de stock mín.",
                            value = state.minStockAlert,
                            onValueChange = { onEvent(AddProductEvent.OnMinStockAlertChanged(it)) },
                            placeholder = "Ej. 5",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Error
            state.error?.let {
                Text(
                    text = it,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // Add category dialog
    if (state.showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(AddProductEvent.OnDismissAddCategoryDialog) },
            title = { Text("Nueva categoría") },
            text = {
                VentlyTextField(
                    label = "Nombre",
                    value = state.newCategoryName,
                    onValueChange = { onEvent(AddProductEvent.OnNewCategoryNameChanged(it)) },
                    placeholder = "Ej. Calzado",
                )
            },
            confirmButton = {
                TextButton(onClick = { onEvent(AddProductEvent.OnSaveCategory) }) {
                    Text("Guardar", color = Primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(AddProductEvent.OnDismissAddCategoryDialog) }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

// ─────────────────────────────────────────────
// Components
// ─────────────────────────────────────────────

@Composable
private fun SectionCard(
    title: String,
    accentColor: Color? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Outline,
            letterSpacing = 0.5.sp,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLowest)
                .then(
                    if (accentColor != null)
                        Modifier.border(
                            width = 1.dp,
                            color = OutlineVariant,
                            shape = RoundedCornerShape(12.dp),
                        )
                    else
                        Modifier.border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun VentlyTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Outline) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = OutlineVariant,
            ),
        )
    }
}

@Composable
private fun PriceTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("0.00", color = Outline) },
            prefix = { Text("S/  ", color = Outline) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = OutlineVariant,
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    categories: List<Category>,
    selected: Category?,
    onSelect: (Category?) -> Unit,
    onAddNew: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = "Categoría",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = selected?.name ?: "Seleccionar",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = OutlineVariant,
                ),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            onSelect(category)
                            expanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Text("Nueva categoría", color = Primary)
                        }
                    },
                    onClick = {
                        expanded = false
                        onAddNew()
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitOfMeasureDropdown(
    selected: String,
    onSelect: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = unitOfMeasureOptions.find { it.first == selected }?.second ?: selected

    Column {
        Text(
            text = "Unidad de medida",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = OutlineVariant,
                ),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                unitOfMeasureOptions.forEach { (code, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            onSelect(code)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun IgvTypeSelector(
    selected: IgvType,
    onSelect: (IgvType) -> Unit,
) {
    Column {
        Text(
            text = "Tipo de IGV",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(OutlineVariant.copy(alpha = 0.2f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            IgvType.entries.forEach { igvType ->
                val isSelected = selected == igvType
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Primary else Color.Transparent)
                        .clickable { onSelect(igvType) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = igvType.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.White else OnSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun StockStepper(
    label: String,
    value: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.5.dp, OutlineVariant, RoundedCornerShape(12.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Reducir",
                    tint = OnSurfaceVariant,
                )
            }
            Text(
                text = "$value",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
            IconButton(
                onClick = onIncrement,
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Aumentar",
                    tint = OnSurfaceVariant,
                )
            }
        }
    }
}