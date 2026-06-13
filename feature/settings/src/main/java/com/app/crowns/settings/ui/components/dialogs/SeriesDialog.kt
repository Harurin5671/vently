package com.app.crowns.settings.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnPrimary
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.ui.components.VentlyModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDialog(
    initialBoletaSeries: String,
    initialBoletaCorrelative: Int,
    initialFacturaSeries: String,
    initialFacturaCorrelative: Int,
    onSave: (boletaSeries: String, boletaCorrelative: Int, facturaSeries: String, facturaCorrelative: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var boletaSeries by remember { mutableStateOf(initialBoletaSeries) }
    var boletaCorrelative by remember { mutableStateOf(initialBoletaCorrelative.toString()) }
    var facturaSeries by remember { mutableStateOf(initialFacturaSeries) }
    var facturaCorrelative by remember { mutableStateOf(initialFacturaCorrelative.toString()) }

    val boletaError = remember(boletaCorrelative) {
        val value = boletaCorrelative.toIntOrNull() ?: 0
        if (value < initialBoletaCorrelative) "No puede ser menor al actual ($initialBoletaCorrelative)" else null
    }

    val facturaError = remember(facturaCorrelative) {
        val value = facturaCorrelative.toIntOrNull() ?: 0
        if (value < initialFacturaCorrelative) "No puede ser menor al actual ($initialFacturaCorrelative)" else null
    }

    VentlyModalBottomSheet(onDismiss = onDismiss, title = "Series y correlativos") {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("BOLETAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Outline)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = boletaSeries,
                        onValueChange = { },
                        label = { Text("Serie") },
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = boletaCorrelative,
                        onValueChange = { if (it.all { c -> c.isDigit() }) boletaCorrelative = it },
                        label = { Text("Correlativo") },
                        placeholder = { Text("0") },
                        singleLine = true,
                        isError = boletaError != null,
                        supportingText = boletaError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                        ),
                        modifier = Modifier.weight(1.5f),
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("FACTURAS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Outline)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = facturaSeries,
                        onValueChange = { },
                        label = { Text("Serie") },
                        readOnly = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                        ),
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = facturaCorrelative,
                        onValueChange = { if (it.all { c -> c.isDigit() }) facturaCorrelative = it },
                        label = { Text("Correlativo") },
                        placeholder = { Text("0") },
                        singleLine = true,
                        isError = facturaError != null,
                        supportingText = facturaError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = OutlineVariant,
                        ),
                        modifier = Modifier.weight(1.5f),
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                onSave(
                    boletaSeries,
                    boletaCorrelative.toIntOrNull() ?: 0,
                    facturaSeries,
                    facturaCorrelative.toIntOrNull() ?: 0,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = boletaCorrelative.isNotBlank() && facturaCorrelative.isNotBlank() && 
                     boletaError == null && facturaError == null,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = MaterialTheme.shapes.medium,
        ) {
            Text("Guardar", color = OnPrimary, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))
    }
}
