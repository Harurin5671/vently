package com.app.crowns.stock.ui.addproduct.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.product.model.Category
import com.app.crowns.ui.components.VentlyModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
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
            color = if (isError) ErrorBright else OnSurfaceVariant,
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
                        contentColor = OnPrimary,
                        disabledContainerColor = Primary.copy(alpha = 0.38f),
                        disabledContentColor = OnPrimary,
                    ),
                ) {
                    if (isSavingCategory) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = OnPrimary,
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