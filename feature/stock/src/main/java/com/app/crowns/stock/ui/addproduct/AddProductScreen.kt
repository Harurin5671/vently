package com.app.crowns.stock.ui.addproduct

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.*
import com.app.crowns.ui.components.VentlyImageSelector
import androidx.compose.ui.platform.LocalLocale
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.crowns.stock.ui.addproduct.components.AddProductSkeleton
import com.app.crowns.stock.ui.addproduct.components.CategoryDropdown
import com.app.crowns.stock.ui.addproduct.components.IgvTypeSelector
import com.app.crowns.stock.ui.addproduct.components.PriceTextField
import com.app.crowns.stock.ui.addproduct.components.SectionCard
import com.app.crowns.stock.ui.addproduct.components.StockStepper
import com.app.crowns.stock.ui.addproduct.components.UnitOfMeasureDropdown
import com.app.crowns.stock.ui.addproduct.components.VentlyTextField

@Composable
fun AddProductScreen(
    onNavigateBack: () -> Unit,
    productId: String? = null,
    viewModel: AddProductViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Determinar si estamos cargando inicialmente un producto para editar
    val isEditing = productId != null
    val isInitialLoading = isEditing && state.editingProductId == null

    LaunchedEffect(productId) {
        if (!productId.isNullOrBlank()) {
            viewModel.onEvent(AddProductEvent.OnLoadProductForEdit(productId))
        }
    }

    LaunchedEffect(state.error, state.showAddCategoryDialog) {
        val error = state.error
        if (error != null && !state.showAddCategoryDialog && state.newCategoryName.isBlank()) {
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(AddProductEvent.OnDismissError)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is AddProductNavigationEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    AddProductContent(
        state = state.copy(isLoading = state.isLoading || isInitialLoading),
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductContent(
    state: AddProductUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (AddProductEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        containerColor = Surface,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.editingProductId != null) "Editar producto" else "Nuevo producto",
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
                    enabled = !state.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = OnPrimary,
                    ),
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = OnPrimary,
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
                            text = if (state.editingProductId != null) "Guardar cambios" else "Guardar producto",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.isLoading) {
            AddProductSkeleton(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(Modifier.height(4.dp))

            // Image Selector
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                VentlyImageSelector(
                    selectedImageUri = state.imageUri,
                    onImageSelected = { onEvent(AddProductEvent.OnImageSelected(it)) },
                    size = 100.dp,
                    shape = RoundedCornerShape(16.dp),
                    existingImageUrl = state.existingImageUrl,
                )
            }

            // Basic info
            SectionCard(title = "INFORMACIÓN BÁSICA") {
                VentlyTextField(
                    label = "Nombre del producto *",
                    value = state.name,
                    onValueChange = { onEvent(AddProductEvent.OnNameChanged(it)) },
                    placeholder = "Ej. Zapatillas Urban Pro",
                    isError = state.showErrors && state.name.isBlank(),
                    supportingText = if (state.showErrors && state.name.isBlank()) "El nombre es obligatorio" else null
                )
                VentlyTextField(
                    label = "Descripción",
                    value = state.description,
                    onValueChange = { onEvent(AddProductEvent.OnDescriptionChanged(it)) },
                    placeholder = "Describe brevemente el producto...",
                    minLines = 3,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    SkuInputField(
                        sku = state.sku,
                        onSkuChanged = { onEvent(AddProductEvent.OnSkuChanged(it)) },
                        modifier = Modifier.weight(1f),
                    )

                    CategoryDropdown(
                        categories = state.categories,
                        selected = state.selectedCategory,
                        onSelect = { onEvent(AddProductEvent.OnCategorySelected(it)) },
                        onEditCategory = { onEvent(AddProductEvent.OnEditCategorySelected(it)) },
                        newCategoryName = state.newCategoryName,
                        editingCategoryId = state.editingCategoryId,
                        onNewCategoryNameChange = { onEvent(AddProductEvent.OnNewCategoryNameChanged(it)) },
                        onSaveCategory = { onEvent(AddProductEvent.OnSaveCategory) },
                        onDismissNewCategory = { onEvent(AddProductEvent.OnDismissAddCategoryDialog) },
                        isSavingCategory = state.isSaving,
                        categoryError = state.error,
                        modifier = Modifier.weight(1f),
                        isError = state.showErrors && state.selectedCategory == null,
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
                        isError = state.showErrors && state.salePrice.toDoubleOrNull() == null
                    )
                    PriceTextField(
                        label = "Precio de costo *",
                        value = state.costPrice,
                        onValueChange = { onEvent(AddProductEvent.OnCostPriceChanged(it)) },
                        modifier = Modifier.weight(1f),
                        isError = state.showErrors && state.costPrice.toDoubleOrNull() == null
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
                        color = if (state.profitMargin >= 0) Success else ErrorBright,
                    )
                }
            }

            // SUNAT
            SectionCard(
                title = "CONFIGURACIÓN SUNAT",
                accentColor = StockLow,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountBalance,
                        contentDescription = null,
                        tint = StockLow,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = "CUMPLIMIENTO FISCAL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StockLow,
                        letterSpacing = 0.8.sp,
                    )
                }

                UnitOfMeasureDropdown(
                    selected = state.unitOfMeasure,
                    units = state.unitsOfMeasure,
                    onSelect = { onEvent(AddProductEvent.OnUnitOfMeasureSelected(it)) },
                    isError = state.showErrors && state.unitOfMeasure.isBlank()
                )

                IgvTypeSelector(
                    selected = state.igvType,
                    types = state.igvTypes.filter { it.isSellableForSale },
                    onSelect = { onEvent(AddProductEvent.OnIgvTypeSelected(it)) },
                    isError = state.showErrors && state.igvType == null
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
                            onValueChange = { onEvent(AddProductEvent.OnCurrentStockChanged(it)) },
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

}

@Composable
private fun SkuInputField(
    sku: String,
    onSkuChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isActive = sku.isNotEmpty()
    val isCorrect = sku.matches(Regex("^[A-Z]{3}-[0-9]{3}$"))
    val textColor = when {
        isActive && !isCorrect -> ErrorBright
        isCorrect -> Success
        else -> OnSurface
    }
    val borderColor = when {
        isActive && !isCorrect -> ErrorBright
        isCorrect -> Success
        else -> OutlineVariant
    }

    var fieldValue by remember { mutableStateOf(TextFieldValue(sku, TextRange(sku.length))) }
    LaunchedEffect(sku) {
        if (sku != fieldValue.text) {
            fieldValue = TextFieldValue(sku, TextRange(sku.length))
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "SKU",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        BasicTextField(
            value = fieldValue,
            onValueChange = { tfv ->
                val clean = tfv.text.replace("-", "").uppercase()
                val letters = clean.filter { it.isLetter() }.take(3)
                val digits = clean.filter { it.isDigit() }.take(3)
                val newSku = if (letters.length == 3 && digits.isNotEmpty()) "$letters-$digits" else letters
                val newCursor = if (newSku.length > tfv.text.length) newSku.length
                                else minOf(tfv.selection.end, newSku.length)
                fieldValue = TextFieldValue(newSku, TextRange(newCursor))
                onSkuChanged(newSku)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (sku.isEmpty()) {
                        Text(
                            text = "AAA-000",
                            color = Outline,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 2.sp,
                        )
                    }
                    innerTextField()
                }
            },
        )
        if (isActive && !isCorrect) {
            Text(
                text = "Formato: 3 letras y 3 números (Ej. ABC-123)",
                fontSize = 11.sp,
                color = ErrorBright,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp),
            )
        }
    }
}