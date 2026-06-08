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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.catalog.entities.IgvTypeEntity
import com.app.crowns.domain.catalog.entities.UnitOfMeasureEntity
import com.app.crowns.ui.components.VentlyImageSelector
import com.app.crowns.ui.components.VentlyModalBottomSheet
import androidx.compose.ui.platform.LocalLocale
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.crowns.ui.components.shimmerEffect

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
        if (state.error != null && !state.showAddCategoryDialog && state.newCategoryName.isBlank()) {
            snackbarHostState.showSnackbar(state.error!!)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SKU",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurfaceVariant,
                        )
                        Spacer(Modifier.height(4.dp))

                        val isSkuFieldActive = state.sku.isNotEmpty()
                        val isSkuCorrect = state.sku.matches(Regex("^[A-Z]{3}-[0-9]{3}$"))
                        val skuColor = when {
                            isSkuFieldActive && !isSkuCorrect -> Color(0xFFEF4444)
                            isSkuCorrect -> Color(0xFF10B981)
                            else -> OnSurface
                        }
                        val borderColor = when {
                            isSkuFieldActive && !isSkuCorrect -> Color(0xFFEF4444)
                            isSkuCorrect -> Color(0xFF10B981)
                            else -> OutlineVariant
                        }

                        var skuFieldValue by remember { mutableStateOf(TextFieldValue(state.sku, TextRange(state.sku.length))) }
                        LaunchedEffect(state.sku) {
                            if (state.sku != skuFieldValue.text) {
                                skuFieldValue = TextFieldValue(state.sku, TextRange(state.sku.length))
                            }
                        }

                        BasicTextField(
                            value = skuFieldValue,
                            onValueChange = { tfv ->
                                val clean = tfv.text.replace("-", "").uppercase()
                                val letters = clean.filter { it.isLetter() }.take(3)
                                val digits = clean.filter { it.isDigit() }.take(3)
                                val newSku = if (letters.length == 3 && digits.isNotEmpty()) {
                                    "$letters-$digits"
                                } else {
                                    letters
                                }
                                // When dash is auto-inserted the raw length jumps by 2,
                                // so we always place the cursor at the end of the new value.
                                val newCursor = if (newSku.length > tfv.text.length) {
                                    newSku.length
                                } else {
                                    minOf(tfv.selection.end, newSku.length)
                                }
                                skuFieldValue = TextFieldValue(newSku, TextRange(newCursor))
                                onEvent(AddProductEvent.OnSkuChanged(newSku))
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
                                color = skuColor,
                                textAlign = TextAlign.Center,
                                letterSpacing = 2.sp,
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    if (state.sku.isEmpty()) {
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
                        if (isSkuFieldActive && !isSkuCorrect) {
                            Text(
                                text = "Formato: 3 letras y 3 números (Ej. ABC-123)",
                                fontSize = 11.sp,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                    
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
                        color = if (state.profitMargin >= 0) Color(0xFF10B981) else Color(0xFFEF4444),
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
                    units = state.unitsOfMeasure,
                    onSelect = { onEvent(AddProductEvent.OnUnitOfMeasureSelected(it)) },
                    isError = state.showErrors && state.unitOfMeasure.isBlank()
                )

                IgvTypeSelector(
                    selected = state.igvType,
                    types = state.igvTypes,
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

// ─────────────────────────────────────────────
// Components
// ─────────────────────────────────────────────

@Composable
private fun AddProductSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(4.dp))

        // Image avatar skeleton
        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }

        // Section skeleton helper
        @Composable
        fun sectionSkeleton(rows: Int) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.width(120.dp).height(14.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .shimmerEffect()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    repeat(rows) {
                        Box(modifier = Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
                    }
                }
            }
        }

        sectionSkeleton(rows = 3) // Información básica
        sectionSkeleton(rows = 2) // Precios
        sectionSkeleton(rows = 2) // SUNAT
        sectionSkeleton(rows = 1) // Control de stock

        Spacer(Modifier.height(80.dp))
    }
}

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
    supportingText: String? = null,
    isError: Boolean = false,
    isSuccess: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = when {
                isError -> Color(0xFFEF4444)
                isSuccess -> Color(0xFF10B981)
                !enabled -> OnSurfaceVariant.copy(alpha = 0.38f)
                else -> OnSurfaceVariant
            },
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (keyboardType == KeyboardType.Number) {
                    if (newValue.all { it.isDigit() }) onValueChange(newValue)
                } else {
                    onValueChange(newValue)
                }
            },
            placeholder = { Text(placeholder, color = Outline) },
            supportingText = supportingText?.let {
                {
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        color = if (isError) Color(0xFFEF4444) else Outline,
                    )
                }
            },
            isError = isError,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isSuccess) Color(0xFF10B981) else Primary,
                unfocusedBorderColor = when {
                    isError -> Color(0xFFEF4444)
                    isSuccess -> Color(0xFF10B981)
                    else -> OutlineVariant
                },
                errorBorderColor = Color(0xFFEF4444),
                errorSupportingTextColor = Color(0xFFEF4444),
                disabledBorderColor = OutlineVariant.copy(alpha = 0.38f),
                disabledTextColor = OnSurface.copy(alpha = 0.38f),
                disabledPlaceholderColor = Outline.copy(alpha = 0.38f),
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
    isError: Boolean = false,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.count { it == '.' } <= 1 && newValue.all { it.isDigit() || it == '.' }) {
                    onValueChange(newValue)
                }
            },
            placeholder = { Text("0.00", color = Outline) },
            prefix = { Text("S/  ", color = Outline) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Color(0xFFEF4444) else Primary,
                unfocusedBorderColor = if (isError) Color(0xFFEF4444) else OutlineVariant,
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
    onEditCategory: (Category) -> Unit,
    newCategoryName: String,
    editingCategoryId: String?,
    onNewCategoryNameChange: (String) -> Unit,
    onSaveCategory: () -> Unit,
    onDismissNewCategory: () -> Unit,
    isSavingCategory: Boolean,
    categoryError: String?,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    var showSheet by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredCategories = remember(searchQuery, categories) {
        if (searchQuery.isBlank()) categories
        else categories.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Vuelve a la lista cuando el guardado termina sin error
    val previouslySaving = remember { mutableStateOf(false) }
    LaunchedEffect(isSavingCategory, categoryError) {
        if (previouslySaving.value && !isSavingCategory && categoryError == null) {
            showForm = false
            onNewCategoryNameChange("")
        }
        previouslySaving.value = isSavingCategory
    }

    Column(modifier = modifier) {
        Text(
            text = "Categoría *",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
        )
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = selected?.name ?: "Seleccionar",
            onValueChange = {},
            readOnly = true,
            isError = isError,
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSheet = true },
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = if (isError) Color(0xFFEF4444) else OutlineVariant,
                disabledTextColor = OnSurface,
                disabledTrailingIconColor = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
            ),
        )
    }

    if (showSheet) {
        VentlyModalBottomSheet(
            onDismiss = {
                if (!isSavingCategory) {
                    showSheet = false
                    showForm = false
                    searchQuery = ""
                    if (showForm) {
                        onDismissNewCategory()
                        onNewCategoryNameChange("")
                    }
                }
            },
            title = if (showForm) (if (editingCategoryId != null) "Editar categoría" else "Nueva categoría") else "Categoría",
            onBack = if (showForm) {
                {
                    showForm = false
                    onNewCategoryNameChange("")
                    onDismissNewCategory()
                }
            } else null,
            searchQuery = if (!showForm) searchQuery else null,
            onSearchQueryChange = if (!showForm) ({ searchQuery = it }) else null,
            searchPlaceholder = "Buscar categoría...",
        ) {
            if (showForm) {
                VentlyTextField(
                    label = "Nombre de la categoría",
                    value = newCategoryName,
                    onValueChange = onNewCategoryNameChange,
                    placeholder = "Ej. Calzado, Electrónica...",
                    enabled = !isSavingCategory,
                    isError = categoryError != null,
                    supportingText = categoryError,
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onSaveCategory,
                    enabled = !isSavingCategory && newCategoryName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                        disabledContainerColor = Primary.copy(alpha = 0.38f),
                        disabledContentColor = Color.White,
                    ),
                ) {
                    if (isSavingCategory) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Text(
                            text = if (editingCategoryId != null) "Guardar cambios" else "Guardar categoría",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 420.dp)) {
                    items(filteredCategories) { category ->
                        val isSelected = category.id == selected?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelect(category)
                                    showSheet = false
                                    searchQuery = ""
                                }
                                .background(
                                    if (isSelected) PrimaryContainer.copy(alpha = 0.15f) else Color.Transparent
                                )
                                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp, end = 0.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = category.name,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Primary else OnSurface,
                                modifier = Modifier.weight(1f),
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Primary,
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .size(18.dp),
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onEditCategory(category)
                                        showForm = true
                                    },
                                    modifier = Modifier.size(40.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Editar categoría",
                                        tint = OnSurfaceVariant,
                                        modifier = Modifier.size(17.dp),
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = OutlineVariant.copy(alpha = 0.4f))
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showForm = true }
                                .padding(horizontal = 4.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Primary.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                            Text(
                                text = "Nueva categoría",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitOfMeasureDropdown(
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
            color = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
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
                    tint = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSheet = true },
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = if (isError) Color(0xFFEF4444) else OutlineVariant,
                disabledTextColor = OnSurface,
                disabledTrailingIconColor = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IgvTypeSelector(
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
            color = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
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
                    tint = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showSheet = true },
            enabled = false,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = if (isError) Color(0xFFEF4444) else OutlineVariant,
                disabledTextColor = OnSurface,
                disabledTrailingIconColor = if (isError) Color(0xFFEF4444) else OnSurfaceVariant,
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

@Composable
private fun StockStepper(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
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
            BasicTextField(
                value = value.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onValueChange(0)
                    } else {
                        it.toIntOrNull()?.let { newValue ->
                            onValueChange(newValue)
                        }
                    }
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface,
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
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