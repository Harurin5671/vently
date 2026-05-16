package com.app.crowns.invoice.ui.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.invoice.domain.model.Invoice
import com.app.crowns.invoice.domain.model.InvoiceType
import com.app.crowns.invoice.domain.store.InvoiceDraftStore
import com.app.crowns.invoice.ui.create.InvoicePreviewUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

sealed class InvoicePreviewNavigationEvent {
    data object NavigateBack : InvoicePreviewNavigationEvent()
    data object InvoiceEmitted : InvoicePreviewNavigationEvent()
}

@HiltViewModel
class InvoicePreviewViewModel @Inject constructor(
    private val draftStore: InvoiceDraftStore,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(InvoicePreviewUiState())
    val state: StateFlow<InvoicePreviewUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<InvoicePreviewNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    val invoice: Invoice? get() = draftStore.draft.value

    init {
        generatePdfPreview()
    }

    fun onShowEmitDialog() = _state.update { it.copy(showEmitDialog = true) }
    fun onDismissEmitDialog() = _state.update { it.copy(showEmitDialog = false) }

    fun onConfirmEmit() {
        viewModelScope.launch {
            _state.update { it.copy(isEmitting = true, showEmitDialog = false) }
            // mock — aquí irá la llamada real a SUNAT
            kotlinx.coroutines.delay(2_000)
            draftStore.clear()
            _state.update { it.copy(isEmitting = false) }
            _navigationEvent.send(InvoicePreviewNavigationEvent.InvoiceEmitted)
        }
    }

    private fun generatePdfPreview() {
        val draft = draftStore.draft.value ?: return
        viewModelScope.launch {
            _state.update { it.copy(isGeneratingPdf = true) }
            val pages = withContext(Dispatchers.IO) {
                val pdfFile = generatePdf(draft)
                renderPdfToPages(pdfFile)
            }
            _state.update { it.copy(isGeneratingPdf = false, pdfPages = pages) }
        }
    }

    private fun generatePdf(invoice: Invoice): File {
        val pageWidth = 595   // A4 width in points
        val pageHeight = 842  // A4 height in points
        val margin = 40f
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paintTitle = Paint().apply {
            color = Color.parseColor("#1c1a25")
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val paintLabel = Paint().apply {
            color = Color.parseColor("#797588")
            textSize = 11f
            isAntiAlias = true
        }
        val paintBody = Paint().apply {
            color = Color.parseColor("#1c1a25")
            textSize = 13f
            isAntiAlias = true
        }
        val paintBold = Paint().apply {
            color = Color.parseColor("#1c1a25")
            textSize = 13f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val paintPrimary = Paint().apply {
            color = Color.parseColor("#6c47ff")
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val paintLine = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            strokeWidth = 0.5f
            isAntiAlias = true
        }
        val paintWatermark = Paint().apply {
            color = Color.parseColor("#E8E8F0")
            textSize = 60f
            isFakeBoldText = true
            alpha = 80
            isAntiAlias = true
        }

        var y = margin + 20f

        // Watermark
        canvas.save()
        canvas.rotate(-30f, pageWidth / 2f, pageHeight / 2f)
        canvas.drawText("BORRADOR", pageWidth / 2f - 120f, pageHeight / 2f, paintWatermark)
        canvas.restore()

        // Header — business name
        canvas.drawText("Vently", margin, y, paintTitle)

        // Invoice type + number (right side)
        val invoiceTypeLabel = when (invoice.type) {
            InvoiceType.BOLETA -> "BOLETA ELECTRÓNICA"
            InvoiceType.FACTURA -> "FACTURA ELECTRÓNICA"
        }
        val invoiceNumber = when (invoice.type) {
            InvoiceType.BOLETA -> "B001-00001"
            InvoiceType.FACTURA -> "F001-00001"
        }
        canvas.drawText(invoiceTypeLabel, pageWidth - margin - 180f, y, paintLabel)
        y += 20f
        canvas.drawText(invoiceNumber, pageWidth - margin - 150f, y, paintPrimary)
        y += 20f

        val dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        canvas.drawText(dateStr, pageWidth - margin - 100f, y, paintLabel)
        y += 30f

        // Divider
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 20f

        // Client section
        canvas.drawText("Cliente:", margin, y, paintLabel)
        y += 18f
        val clientDisplay = when (invoice.type) {
            InvoiceType.BOLETA -> invoice.clientName?.ifBlank { "Cliente general" } ?: "Cliente general"
            InvoiceType.FACTURA -> invoice.clientBusinessName ?: ""
        }
        canvas.drawText(clientDisplay, margin, y, paintBold)
        if (invoice.type == InvoiceType.FACTURA && !invoice.clientRuc.isNullOrBlank()) {
            y += 18f
            canvas.drawText("RUC: ${invoice.clientRuc}", margin, y, paintBody)
        }
        y += 30f

        // Divider
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 20f

        // Items header
        canvas.drawText("Descripción", margin, y, paintLabel)
        canvas.drawText("Cant.", 340f, y, paintLabel)
        canvas.drawText("P.Unit", 420f, y, paintLabel)
        canvas.drawText("Total", pageWidth - margin - 40f, y, paintLabel)
        y += 16f
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 16f

        // Items
        invoice.items.forEach { item ->
            canvas.drawText(item.name.take(40), margin, y, paintBody)
            canvas.drawText("${item.quantity}", 340f, y, paintBody)
            canvas.drawText(
                String.format(Locale.getDefault(), "%.2f", item.unitPrice),
                420f, y, paintBody
            )
            canvas.drawText(
                String.format(Locale.getDefault(), "%.2f", item.subtotal),
                pageWidth - margin - 60f, y, paintBody
            )
            y += 20f
            canvas.drawLine(margin, y - 4f, pageWidth - margin, y - 4f, paintLine)
        }

        y += 20f

        // Totals
        val subtotal = invoice.subtotal
        val igv = invoice.igv
        val total = invoice.total

        val totalsX = pageWidth - margin - 160f
        canvas.drawText("Subtotal", totalsX, y, paintLabel)
        canvas.drawText(
            "S/ ${String.format(Locale.getDefault(), "%.2f", subtotal)}",
            pageWidth - margin - 60f, y, paintBody
        )
        y += 20f
        canvas.drawText("IGV (18%)", totalsX, y, paintLabel)
        canvas.drawText(
            "S/ ${String.format(Locale.getDefault(), "%.2f", igv)}",
            pageWidth - margin - 60f, y, paintBody
        )
        y += 6f
        canvas.drawLine(totalsX, y, pageWidth - margin, y, paintLine)
        y += 16f
        canvas.drawText("Total", totalsX, y, paintBold)
        canvas.drawText(
            "S/ ${String.format(Locale.getDefault(), "%.2f", total)}",
            pageWidth - margin - 80f, y, paintPrimary
        )

        document.finishPage(page)

        val file = File(context.cacheDir, "invoice_preview.pdf")
        file.outputStream().use { document.writeTo(it) }
        document.close()
        return file
    }

    private fun renderPdfToPages(file: File): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(fd)
        for (i in 0 until renderer.pageCount) {
            val page = renderer.openPage(i)
            val scale = 2f  // retina quality
            val bitmap = Bitmap.createBitmap(
                (page.width * scale).toInt(),
                (page.height * scale).toInt(),
                Bitmap.Config.ARGB_8888,
            )
            bitmap.eraseColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            bitmaps.add(bitmap)
        }
        renderer.close()
        fd.close()
        return bitmaps
    }
}