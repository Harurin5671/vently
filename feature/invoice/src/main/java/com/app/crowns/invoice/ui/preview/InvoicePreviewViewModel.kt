package com.app.crowns.invoice.ui.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.app.crowns.domain.util.generateInvoicePdf
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.invoice.entities.GenerateXmlRequestEntity
import com.app.crowns.domain.invoice.entities.GenerateXmlRequestItemEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.InvoiceItemEntity
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
import javax.inject.Inject
import androidx.core.graphics.createBitmap

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
                val invoiceEntity = draft.toEntity()
                val pages = withContext(Dispatchers.IO) {
                    val pdfFile = generateInvoicePdf(context, invoiceEntity, company)
                    renderPdfToPages(pdfFile)
                }
                _state.update { it.copy(isGeneratingPdf = false, pdfPages = pages) }
            } catch (e: Exception) {
                _state.update { it.copy(isGeneratingPdf = false, error = "Error al generar PDF: ${e.message}") }
            }
        }
    }

    private fun Invoice.toEntity() = InvoiceEntity(
        id = "",
        companyId = "",
        documentType = if (type == InvoiceType.FACTURA) "01" else "03",
        serie = if (type == InvoiceType.FACTURA) "F001" else "B001",
        correlative = 1,
        total = total.toString(),
        status = "BORRADOR",
        statusInfo = null,
        issueDate = LocalDate.now().toString(),
        operationType = "0101",
        currency = "PEN",
        paymentType = "Contado",
        totalDiscount = "0.0",
        totalExportacion = "0.0",
        totalExonerada = "0.0",
        totalInafecta = "0.0",
        totalGravada = subtotal.toString(),
        totalIgv = igv.toString(),
        xmlDocument = "",
        items = items.map { item ->
            InvoiceItemEntity(
                id = "",
                invoiceId = "",
                productId = item.productId,
                order = 0,
                description = item.name,
                unit = "NIU",
                quantity = item.quantity.toString(),
                unitPrice = item.unitPrice.toString(),
                salePrice = item.unitPrice.toString(),
                igvAmount = (item.subtotal * 0.18).toString(), // Estimado para preview
                total = item.subtotal.toString(),
                igvType = "10",
                discountAmount = "0.0"
            )
        },
        customerDocNumber = clientRuc ?: "0",
        customerLegalName = clientBusinessName ?: clientName ?: "CLIENTE VARIOS"
    )

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
}
