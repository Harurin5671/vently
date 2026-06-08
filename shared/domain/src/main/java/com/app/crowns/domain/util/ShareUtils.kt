package com.app.crowns.domain.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

fun Context.shareText(text: String, title: String = "Compartir") {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, title)
    startActivity(shareIntent)
}

/**
 * Abre un archivo PDF utilizando el visor del sistema.
 */
fun Context.openFile(file: File) {
    try {
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(Intent.createChooser(intent, "Abrir comprobante"))
    } catch (e: Exception) {
        Toast.makeText(this, "No se pudo abrir el archivo", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Copia un archivo a un URI (usado para guardar en una carpeta elegida por el usuario).
 */
fun Context.copyFileToUri(file: File, uri: Uri) {
    try {
        contentResolver.openOutputStream(uri)?.use { output ->
            file.inputStream().use { input ->
                input.copyTo(output)
            }
        }
    } catch (e: Exception) {
        Toast.makeText(this, "Error al guardar el archivo", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Comparte el mensaje de texto y opcionalmente el PDF del comprobante.
 */
fun Context.shareInvoice(
    companyName: String,
    companyRuc: String,
    documentName: String,
    documentId: String,
    customerName: String,
    total: String,
    pdfFile: File? = null
) {
    val shareText = """
        *¡Hola $customerName!* 📄
        
        Te enviamos el detalle de tu comprobante electrónico de *$companyName*.
        
        📌 *Detalle del documento:*
        • *$documentName:* $documentId
        • *Emisor:* $companyName (RUC: $companyRuc)
        • *Monto Total:* S/ $total
        
        ¡Gracias por tu preferencia! ✨
    """.trimIndent()

    val intent = Intent().apply {
        if (pdfFile != null && pdfFile.exists()) {
            action = Intent.ACTION_SEND
            val contentUri: Uri = FileProvider.getUriForFile(
                this@shareInvoice,
                "${packageName}.fileprovider",
                pdfFile
            )
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
    }

    startActivity(Intent.createChooser(intent, "Enviar comprobante"))
}
