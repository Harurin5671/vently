package com.app.crowns.domain.util

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.graphics.toColorInt
import androidx.core.graphics.withRotation
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.documentName
import com.app.crowns.domain.invoice.entities.formattedId
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun generateInvoicePdf(
    context: Context,
    invoice: InvoiceEntity,
    company: CompanyInfo?
): File {
    val pageWidth = 595   // A4 width in points
    val pageHeight = 842  // A4 height in points
    val margin = 40f
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = document.startPage(pageInfo)
    val canvas = page.canvas

    val paintTitle = Paint().apply {
        color = "#1c1a25".toColorInt()
        textSize = 18f
        isFakeBoldText = true
        isAntiAlias = true
    }
    val paintLabel = Paint().apply {
        color = "#797588".toColorInt()
        textSize = 10f
        isAntiAlias = true
    }
    val paintBody = Paint().apply {
        color = "#1c1a25".toColorInt()
        textSize = 12f
        isAntiAlias = true
    }
    val paintBold = Paint().apply {
        color = "#1c1a25".toColorInt()
        textSize = 12f
        isFakeBoldText = true
        isAntiAlias = true
    }
    val paintPrimary = Paint().apply {
        color = "#6c47ff".toColorInt()
        textSize = 18f
        isFakeBoldText = true
        isAntiAlias = true
    }
    val paintLine = Paint().apply {
        color = "#E2E8F0".toColorInt()
        strokeWidth = 0.5f
        isAntiAlias = true
    }
    val paintWatermark = Paint().apply {
        color = "#F1F5F9".toColorInt()
        textSize = 60f
        isFakeBoldText = true
        alpha = 40
        isAntiAlias = true
    }

    var y = margin + 20f
    val leftMaxWidth = 320f

    // Watermark (solo si NO está aceptada o es un borrador)
    if (invoice.status != "ACEPTADA") {
        canvas.withRotation(-30f, pageWidth / 2f, pageHeight / 2f) {
            drawText("BORRADOR", pageWidth / 2f - 120f, pageHeight / 2f, paintWatermark)
        }
    }

    // Branding (App Name)
    val paintBranding = Paint(paintPrimary).apply {
        textSize = 10f
        alpha = 100
    }
    canvas.drawText("Vently", margin, margin - 10f, paintBranding)

    // Header Right
    val paintRightLabel = Paint(paintLabel).apply { textAlign = Paint.Align.RIGHT }
    val paintRightPrimary = Paint(paintPrimary).apply { textAlign = Paint.Align.RIGHT }
    
    var rightY = margin + 18f
    val invoiceTypeLabel = (invoice.documentName + " Electrónica").uppercase()
    
    canvas.drawText(invoiceTypeLabel, pageWidth - margin, rightY, paintRightLabel)
    rightY += 18f
    canvas.drawText(invoice.formattedId, pageWidth - margin, rightY, paintRightPrimary)
    rightY += 18f
    
    val dateStr = try {
        val datePart = invoice.issueDate.take(10)
        LocalDate.parse(datePart).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        invoice.issueDate
    }
    canvas.drawText("Fecha: $dateStr", pageWidth - margin, rightY, paintRightLabel)

    // Header Left
    val businessName = company?.businessName?.uppercase() ?: "MI EMPRESA"
    y = drawWrappedText(businessName, margin, y, leftMaxWidth, paintTitle, canvas)

    if (company != null) {
        y += 4f
        canvas.drawText("RUC: ${company.documentNumber}", margin, y, paintBody)
        y += 14f
        y = drawWrappedText(company.address, margin, y, leftMaxWidth, paintLabel, canvas)
        y = drawWrappedText("${company.district}, ${company.province} - ${company.department}", margin, y, leftMaxWidth, paintLabel, canvas)
    }

    y = maxOf(y, rightY) + 25f
    canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
    y += 20f

    // Client
    canvas.drawText("DATOS DEL RECEPTOR", margin, y, paintLabel)
    y += 18f
    val clientLabel = if (invoice.documentType == "01") "Razón Social: " else "Nombre: "
    canvas.drawText(clientLabel, margin, y, paintBody)
    y = drawWrappedText(invoice.customerLegalName, margin + 85f, y, pageWidth - margin - (margin + 85f), paintBold, canvas)

    if (invoice.customerDocNumber.isNotBlank()) {
        val docLabel = if (invoice.documentType == "01") "RUC: " else "Documento: "
        canvas.drawText(docLabel, margin, y, paintBody)
        canvas.drawText(invoice.customerDocNumber, margin + 85f, y, paintBody)
        y += 16f
    }
    y += 15f
    canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
    y += 20f

    // Items
    canvas.drawText("Descripción", margin, y, paintLabel)
    canvas.drawText("Cant.", 340f, y, paintLabel)
    canvas.drawText("P.Unit", 410f, y, paintLabel)
    canvas.drawText("Total", pageWidth - margin, y, paintRightLabel)
    y += 14f
    canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
    y += 16f

    invoice.items.forEach { item ->
        val startY = y
        val descNextY = drawWrappedText(item.description, margin, y, 280f, paintBody, canvas)
        canvas.drawText(item.quantity, 340f, startY, paintBody)
        canvas.drawText(
            String.format(Locale.getDefault(), "%.2f", item.unitPrice.toDoubleOrNull() ?: 0.0),
            410f, startY, paintBody
        )
        canvas.drawText(
            String.format(Locale.getDefault(), "%.2f", item.total.toDoubleOrNull() ?: 0.0),
            pageWidth - margin, startY, paintRightLabel
        )
        y = maxOf(descNextY, startY + 16f)
        y += 4f
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 16f
    }
    y += 10f

    // Totals
    val totalsX = pageWidth - margin - 160f
    canvas.drawText("Subtotal", totalsX, y, paintLabel)
    canvas.drawText("S/ ${String.format(Locale.getDefault(), "%.2f", invoice.totalGravada.toDoubleOrNull() ?: 0.0)}", pageWidth - margin - 60f, y, paintBody)
    y += 20f
    canvas.drawText("IGV (18%)", totalsX, y, paintLabel)
    canvas.drawText("S/ ${String.format(Locale.getDefault(), "%.2f", invoice.totalIgv.toDoubleOrNull() ?: 0.0)}", pageWidth - margin - 60f, y, paintBody)
    y += 6f
    canvas.drawLine(totalsX, y, pageWidth - margin, y, paintLine)
    y += 16f
    canvas.drawText("Total", totalsX, y, paintBold)
    canvas.drawText("S/ ${String.format(Locale.getDefault(), "%.2f", invoice.total.toDoubleOrNull() ?: 0.0)}", pageWidth - margin - 80f, y, paintPrimary)

    // Footer
    var footerY = pageHeight - margin - 60f
    canvas.drawLine(margin, footerY, pageWidth - margin, footerY, paintLine)
    footerY += 20f
    val footerText = "Representación impresa de la $invoiceTypeLabel"
    canvas.drawText(footerText, (pageWidth - paintLabel.measureText(footerText)) / 2f, footerY, paintLabel)
    footerY += 16f
    val footerNote = "Consulte la validez de este comprobante en el portal de la SUNAT"
    canvas.drawText(footerNote, (pageWidth - paintLabel.measureText(footerNote)) / 2f, footerY, paintLabel)

    document.finishPage(page)
    val dir = File(context.cacheDir, "invoices")
    if (!dir.exists()) dir.mkdirs()
    val file = File(dir, "Invoice_${invoice.formattedId}.pdf")
    file.outputStream().use { document.writeTo(it) }
    document.close()
    return file
}

private fun drawWrappedText(text: String, x: Float, y: Float, maxWidth: Float, paint: Paint, canvas: android.graphics.Canvas): Float {
    val words = text.split(" ")
    var currentLine = ""
    var currentY = y
    val lineHeight = paint.textSize * 1.2f
    for (word in words) {
        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
        if (paint.measureText(testLine) > maxWidth) {
            if (currentLine.isNotEmpty()) {
                canvas.drawText(currentLine, x, currentY, paint)
                currentY += lineHeight
            }
            currentLine = word
        } else currentLine = testLine
    }
    canvas.drawText(currentLine, x, currentY, paint)
    return currentY + lineHeight
}
