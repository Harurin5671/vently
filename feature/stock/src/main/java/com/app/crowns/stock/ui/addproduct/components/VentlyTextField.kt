package com.app.crowns.stock.ui.addproduct.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*

@Composable
fun VentlyTextField(
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
                isError -> ErrorBright
                isSuccess -> Success
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
                        color = if (isError) ErrorBright else Outline,
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
                focusedBorderColor = if (isSuccess) Success else Primary,
                unfocusedBorderColor = when {
                    isError -> ErrorBright
                    isSuccess -> Success
                    else -> OutlineVariant
                },
                errorBorderColor = ErrorBright,
                errorSupportingTextColor = ErrorBright,
                disabledBorderColor = OutlineVariant.copy(alpha = 0.38f),
                disabledTextColor = OnSurface.copy(alpha = 0.38f),
                disabledPlaceholderColor = Outline.copy(alpha = 0.38f),
            ),
        )
    }
}