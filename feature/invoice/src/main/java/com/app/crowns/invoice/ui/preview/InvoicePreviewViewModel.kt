package com.app.crowns.invoice.ui.preview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.util.generateInvoicePdf
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.company.usecase.GetDocumentSeriesUseCase
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
    private val getDocumentSeriesUseCase: GetDocumentSeriesUseCase,
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

        viewModelScope.launch {
            val existingInvoiceId = _state.value.invoiceId
            val alreadySigned = _state.value.isSigned
            _state.update { it.copy(isEmitting = true, showEmitDialog = false, error = null) }

            try {
                val invoiceId: String

                if (existingInvoiceId == null) {
                    val company = getCompanyUseCase().getOrNull()
                    if (company == null) {
                        _state.update { it.copy(isEmitting = false, error = "No se pudo obtener la información de tu empresa") }
                        return@launch
                    }

                    val documentTypeCode = if (draft.type == InvoiceType.FACTURA)
                        ElectronicDocumentTypeEntity.FACTURA else ElectronicDocumentTypeEntity.BOLETA
                    val documentSeriesList = getDocumentSeriesUseCase(company.id).getOrNull()
                    val matchingSeries = documentSeriesList?.find { it.documentType == documentTypeCode }
                    val series = matchingSeries?.serie ?: "${draft.type.prefix}001"
                    val customerDocType = if (draft.type == InvoiceType.FACTURA) "6" else (draft.clientDocumentType ?: "1")

                    val xmlResponse = generateXmlUseCase(GenerateXmlRequestEntity(
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
                    )).getOrThrow()

                    invoiceId = xmlResponse.invoiceId
                    _state.update { it.copy(
                        invoiceId = invoiceId,
                        documentId = xmlResponse.documentId,
                        series = series,
                        correlative = xmlResponse.correlative.toString()
                    ) }
                } else {
                    invoiceId = existingInvoiceId
                }

                if (!alreadySigned) {
                    val signResult = signInvoiceUseCase(invoiceId).getOrThrow()
                    if (!signResult.success) {
                        _state.update { it.copy(isEmitting = false, error = "Error en firma: ${signResult.status}") }
                        return@launch
                    }
                    _state.update { it.copy(isSigned = true) }
                }

                val sunatResult = sendToSunatUseCase(invoiceId).getOrThrow()
                _state.update { it.copy(
                    isEmitting = false,
                    showResultScreen = true,
                    sunatResponse = sunatResult
                ) }

                if (sunatResult.isSuccess) draftStore.clear()

            } catch (e: Exception) {
                _state.update { it.copy(isEmitting = false, error = "Error: ${e.message}") }
            }
        }
    }

    private fun generatePdfPreview() {
        val draft = draftStore.draft.value
        if (draft == null) {
            viewModelScope.launch {
                _navigationEvent.send(InvoicePreviewNavigationEvent.NavigateBack)
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isGeneratingPdf = true, error = null) }
            try {
                val company = getCompanyUseCase().getOrNull()
                val documentTypeCode = if (draft.type == InvoiceType.FACTURA)
                    ElectronicDocumentTypeEntity.FACTURA else ElectronicDocumentTypeEntity.BOLETA
                val seriesList = company?.let { getDocumentSeriesUseCase(it.id).getOrNull() }
                val serie = seriesList?.find { it.documentType == documentTypeCode }?.serie
                val invoiceEntity = draft.toEntity(serie ?: if (draft.type == InvoiceType.FACTURA) "F001" else "B001")
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

    private fun Invoice.toEntity(serie: String = if (type == InvoiceType.FACTURA) "F001" else "B001") = InvoiceEntity(
        id = "",
        companyId = "",
        documentType = if (type == InvoiceType.FACTURA) "01" else "03",
        serie = serie,
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
