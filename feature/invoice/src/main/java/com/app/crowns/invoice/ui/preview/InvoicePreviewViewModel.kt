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
import android.util.Log
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.invoice.entities.GenerateXmlRequestEntity
import com.app.crowns.domain.invoice.entities.GenerateXmlRequestItemEntity
import com.app.crowns.domain.invoice.usecase.GenerateXmlUseCase
import com.app.crowns.domain.invoice.usecase.SendToSunatUseCase
import com.app.crowns.domain.invoice.usecase.SignInvoiceUseCase
import com.app.crowns.invoice.domain.model.Invoice
import com.app.crowns.invoice.domain.model.InvoiceType
import com.app.crowns.invoice.domain.store.InvoiceDraftStore
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
import androidx.core.graphics.toColorInt
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withRotation

sealed class InvoicePreviewNavigationEvent {
    data object NavigateBack : InvoicePreviewNavigationEvent()
    data object InvoiceEmitted : InvoicePreviewNavigationEvent()
}

@HiltViewModel
class InvoicePreviewViewModel @Inject constructor(
    private val draftStore: InvoiceDraftStore,
    private val generateXmlUseCase: GenerateXmlUseCase,
    private val signInvoiceUseCase: SignInvoiceUseCase,
    private val sendToSunatUseCase: SendToSunatUseCase,
    private val getCompanyUseCase: GetCompanyUseCase,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(InvoicePreviewUiState())
    val state: StateFlow<InvoicePreviewUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<InvoicePreviewNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        generatePdfPreview()
    }

    fun onShowEmitDialog() = _state.update { it.copy(showEmitDialog = true) }
    fun onDismissEmitDialog() = _state.update { it.copy(showEmitDialog = false) }

    fun onConfirmEmit() {
        val draft = draftStore.draft.value ?: return
        Log.d("Prueba_Generate_Xml", "VM: Inicio de confirmación de emisión")

        viewModelScope.launch {
            _state.update { it.copy(isEmitting = true, showEmitDialog = false, error = null) }
            
            try {
                // 1. Obtener la empresa
                val company = getCompanyUseCase().getOrNull()
                if (company == null) {
                    _state.update { it.copy(isEmitting = false, error = "No se pudo obtener la información de tu empresa") }
                    return@launch
                }

                // 2. GENERAR XML
                val series = "${draft.type.prefix}001"
                Log.d("Prueba123123", series)
                val customerDocType = if (draft.type == InvoiceType.FACTURA) "6" else (draft.clientDocumentType ?: "1")
                
                val generateRequest = GenerateXmlRequestEntity(
                    companyId = company.id,
                    series = series,
                    issueDate = LocalDate.now().toString(),
                    currency = "PEN",
                    paymentMethod = "Contado",
                    customerDocType = customerDocType,
                    customerDocNumber = draft.clientRuc ?: draft.clientName ?: "0",
                    customerLegalName = draft.clientBusinessName ?: draft.clientName ?: "CLIENTE VARIOS",
                    items = draft.items.map { 
                        GenerateXmlRequestItemEntity(
                            productId = it.productId,
                            quantity = it.quantity.toDouble()
                        )
                    }
                )

                val xmlResponse = generateXmlUseCase(generateRequest).getOrThrow()
                Log.d("Prueba_Generate_Xml", "VM: XML Generado ID: ${xmlResponse.invoiceId}")
                _state.update { it.copy(
                    documentId = xmlResponse.documentId,
                    series = series,
                    correlative = xmlResponse.correlative.toString()
                ) }

                // 3. FIRMAR XML
                Log.d("Prueba_Sign_Invoice", "VM: Iniciando firma para invoiceId: ${xmlResponse.invoiceId}")
                val signResult = signInvoiceUseCase(xmlResponse.invoiceId).getOrThrow()
                Log.d("Prueba_Sign_Invoice", "VM: Firma completada. Success: ${signResult.success}")
                
                if (!signResult.success) {
                    _state.update { it.copy(isEmitting = false, error = "Error en firma: ${signResult.status}") }
                    return@launch
                }

                // 4. ENVIAR A SUNAT
                Log.d("Prueba_Send_Sunat", "VM: Iniciando envío a SUNAT para invoiceId: ${xmlResponse.invoiceId}")
                val sunatResult = sendToSunatUseCase(xmlResponse.invoiceId).getOrThrow()
                Log.d("Prueba_Send_Sunat", "VM: Envío a SUNAT completado. Success: ${sunatResult.success}")
                
                _state.update { it.copy(
                    isEmitting = false,
                    showResultScreen = true,
                    sunatResponse = sunatResult
                ) }
                
                if (sunatResult.isSuccess) {
                    draftStore.clear()
                }

            } catch (e: Exception) {
                Log.e("Prueba_Generate_Xml", "VM: Error en flujo", e)
                _state.update { it.copy(isEmitting = false, error = "Error: ${e.message}") }
            }
        }
    }

    private fun generatePdfPreview() {
        val draft = draftStore.draft.value
        if (draft == null) {
            _state.update { it.copy(isGeneratingPdf = false, error = "No hay datos para la vista previa") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isGeneratingPdf = true, error = null) }
            try {
                val company = getCompanyUseCase().getOrNull()
                val pages = withContext(Dispatchers.IO) {
                    val pdfFile = generatePdf(draft, company)
                    renderPdfToPages(pdfFile)
                }
                _state.update { it.copy(isGeneratingPdf = false, pdfPages = pages) }
            } catch (e: Exception) {
                _state.update { it.copy(isGeneratingPdf = false, error = "Error al generar PDF: ${e.message}") }
            }
        }
    }

    private fun generatePdf(invoice: Invoice, company: com.app.crowns.domain.company.model.CompanyInfo?): File {
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

        // Watermark
        canvas.withRotation(-30f, pageWidth / 2f, pageHeight / 2f) {
            drawText("BORRADOR", pageWidth / 2f - 120f, pageHeight / 2f, paintWatermark)
        }

        // Branding (App Name) - Top Left
        val paintBranding = Paint(paintPrimary).apply {
            textSize = 10f
            alpha = 100
        }
        canvas.drawText("Vently", margin, margin - 10f, paintBranding)

        // Header — Right Side Info (Grouped first to handle baseline)
        val paintRightLabel = Paint(paintLabel).apply { textAlign = Paint.Align.RIGHT }
        val paintRightPrimary = Paint(paintPrimary).apply { textAlign = Paint.Align.RIGHT }
        
        var rightY = margin + 18f
        val invoiceTypeLabel = when (invoice.type) {
            InvoiceType.BOLETA -> "BOLETA DE VENTA"
            InvoiceType.FACTURA -> "FACTURA"
        } + " ELECTRÓNICA"
        
        val invoiceNumber = when (invoice.type) {
            InvoiceType.BOLETA -> "B001-00001"
            InvoiceType.FACTURA -> "F001-00001"
        }
        
        canvas.drawText(invoiceTypeLabel, pageWidth - margin, rightY, paintRightLabel)
        rightY += 18f
        canvas.drawText(invoiceNumber, pageWidth - margin, rightY, paintRightPrimary)
        rightY += 18f
        val dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        canvas.drawText("Fecha: $dateStr", pageWidth - margin, rightY, paintRightLabel)

        // Header — Left Side (Company Info)
        val businessName = company?.businessName?.uppercase() ?: "VENTLY S.A.C."
        y = drawWrappedText(businessName, margin, y, leftMaxWidth, paintTitle, canvas)

        if (company != null) {
            y += 4f
            canvas.drawText("RUC: ${company.documentNumber}", margin, y, paintBody)
            y += 14f
            y = drawWrappedText(company.address, margin, y, leftMaxWidth, paintLabel, canvas)
            y = drawWrappedText("${company.district}, ${company.province} - ${company.department}", margin, y, leftMaxWidth, paintLabel, canvas)
        }

        // Ensure y is below header info
        y = maxOf(y, rightY) + 25f

        // Divider
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 20f

        // Client section
        canvas.drawText("DATOS DEL RECEPTOR", margin, y, paintLabel)
        y += 18f

        val clientLabel = if (invoice.type == InvoiceType.FACTURA) "Razón Social: " else "Nombre: "
        val clientValue = when (invoice.type) {
            InvoiceType.BOLETA -> invoice.clientName?.ifBlank { "CLIENTE VARIOS" } ?: "CLIENTE VARIOS"
            InvoiceType.FACTURA -> invoice.clientBusinessName ?: ""
        }
        
        canvas.drawText(clientLabel, margin, y, paintBody)
        y = drawWrappedText(clientValue, margin + 85f, y, pageWidth - margin - (margin + 85f), paintBold, canvas)

        if (!invoice.clientRuc.isNullOrBlank()) {
            val docLabel = if (invoice.type == InvoiceType.FACTURA) "RUC: " else "Documento: "
            canvas.drawText(docLabel, margin, y, paintBody)
            canvas.drawText(invoice.clientRuc, margin + 85f, y, paintBody)
            y += 16f
        }
        y += 15f

        // Divider
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 20f

        // Items header
        canvas.drawText("Descripción", margin, y, paintLabel)
        canvas.drawText("Cant.", 340f, y, paintLabel)
        canvas.drawText("P.Unit", 410f, y, paintLabel)
        canvas.drawText("Total", pageWidth - margin, y, paintRightLabel)
        y += 14f
        canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
        y += 16f

        // Items
        invoice.items.forEach { item ->
            val startY = y
            val descNextY = drawWrappedText(item.name, margin, y, 280f, paintBody, canvas)
            
            canvas.drawText("${item.quantity}", 340f, startY, paintBody)
            canvas.drawText(
                String.format(Locale.getDefault(), "%.2f", item.unitPrice),
                410f, startY, paintBody
            )
            canvas.drawText(
                String.format(Locale.getDefault(), "%.2f", item.subtotal),
                pageWidth - margin, startY, paintRightLabel
            )
            
            y = maxOf(descNextY, startY + 16f)
            y += 4f
            canvas.drawLine(margin, y, pageWidth - margin, y, paintLine)
            y += 16f
        }

        y += 10f

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

        // Footer (legal text)
        var footerY = pageHeight - margin - 60f
        canvas.drawLine(margin, footerY, pageWidth - margin, footerY, paintLine)
        footerY += 20f
        val footerText = "Representación impresa de la ${invoiceTypeLabel.uppercase()}"
        val footerWidth = paintLabel.measureText(footerText)
        canvas.drawText(footerText, (pageWidth - footerWidth) / 2f, footerY, paintLabel)
        footerY += 16f
        val footerNote = "Consulte la validez de este comprobante en el portal de la SUNAT"
        val footerNoteWidth = paintLabel.measureText(footerNote)
        canvas.drawText(footerNote, (pageWidth - footerNoteWidth) / 2f, footerY, paintLabel)

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
            val bitmap = createBitmap((page.width * scale).toInt(), (page.height * scale).toInt())
            bitmap.eraseColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            bitmaps.add(bitmap)
        }
        renderer.close()
        fd.close()
        return bitmaps
    }

    private fun drawWrappedText(
        text: String,
        x: Float,
        y: Float,
        maxWidth: Float,
        paint: Paint,
        canvas: android.graphics.Canvas
    ): Float {
        val words = text.split(" ")
        var currentLine = ""
        var currentY = y
        val lineHeight = paint.textSize * 1.2f

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val testWidth = paint.measureText(testLine)
            if (testWidth > maxWidth) {
                if (currentLine.isNotEmpty()) {
                    canvas.drawText(currentLine, x, currentY, paint)
                    currentY += lineHeight
                }
                currentLine = word
            } else {
                currentLine = testLine
            }
        }
        canvas.drawText(currentLine, x, currentY, paint)
        return currentY + lineHeight
    }
}
