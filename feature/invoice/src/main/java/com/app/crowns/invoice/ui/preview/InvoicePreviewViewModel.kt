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
import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
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
import com.app.crowns.common.util.toUserFriendlyMessage
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
import android.util.Log

private const val TAG = "InvoicePreview"

sealed class InvoicePreviewNavigationEvent {
    data object NavigateBack : InvoicePreviewNavigationEvent()
    data object InvoiceEmitted : InvoicePreviewNavigationEvent()
    data class ShareInvoice(
        val companyName: String,
        val companyRuc: String,
        val documentName: String,
        val documentId: String,
        val customerName: String,
        val total: String,
        val pdfFile: File?
    ) : InvoicePreviewNavigationEvent()
    data class DownloadPdf(
        val file: File,
        val fileName: String
    ) : InvoicePreviewNavigationEvent()
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
        val draft = draftStore.draft.value ?: run {
            Log.w(TAG, "onConfirmEmit: draft is null, aborting")
            return
        }

        Log.d(TAG, "onConfirmEmit: iniciando emisión | type=${draft.type} client=${draft.clientBusinessName ?: draft.clientName} items=${draft.items.size}")

        viewModelScope.launch {
            val existingInvoiceId = _state.value.invoiceId
            val alreadySigned = _state.value.isSigned
            Log.d(TAG, "onConfirmEmit: existingInvoiceId=$existingInvoiceId alreadySigned=$alreadySigned")
            _state.update { it.copy(isEmitting = true, showEmitDialog = false, error = null) }

            try {
                val invoiceId: String

                if (existingInvoiceId == null) {
                    Log.d(TAG, "generateXml: obteniendo empresa...")
                    val company = getCompanyUseCase().getOrNull()
                    if (company == null) {
                        Log.e(TAG, "generateXml: no se pudo obtener la empresa")
                        _state.update { it.copy(isEmitting = false, error = "No se pudo obtener la información de tu empresa") }
                        return@launch
                    }
                    Log.d(TAG, "generateXml: empresa obtenida id=${company.id} name=${company.businessName}")

                    val documentTypeCode = if (draft.type == InvoiceType.FACTURA)
                        ElectronicDocumentTypeEntity.FACTURA else ElectronicDocumentTypeEntity.BOLETA
                    val documentSeriesList = getDocumentSeriesUseCase(company.id).getOrNull()
                    Log.d(TAG, "generateXml: series disponibles=${documentSeriesList?.map { "${it.documentType}:${it.serie}" }}")
                    val matchingSeries = documentSeriesList?.find { it.documentType == documentTypeCode }
                    val series = matchingSeries?.serie ?: "${draft.type.prefix}001"
                    val customerDocType = if (draft.type == InvoiceType.FACTURA) IdentityDocumentTypeEntity.Type.RUC.code else (draft.clientDocumentType ?: IdentityDocumentTypeEntity.Type.DNI.code)
                    Log.d(TAG, "generateXml: serie=$series docType=$documentTypeCode customerDocType=$customerDocType")

                    val request = GenerateXmlRequestEntity(
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
                    Log.d(TAG, "generateXml: request=$request")

                    val xmlResponse = generateXmlUseCase(request).getOrThrow()
                    Log.i(TAG, "generateXml: OK | invoiceId=${xmlResponse.invoiceId} documentId=${xmlResponse.documentId} correlative=${xmlResponse.correlative}")

                    invoiceId = xmlResponse.invoiceId
                    _state.update { it.copy(
                        invoiceId = invoiceId,
                        documentId = xmlResponse.documentId,
                        series = series,
                        correlative = xmlResponse.correlative.toString()
                    ) }
                } else {
                    Log.d(TAG, "generateXml: reutilizando invoiceId existente=$existingInvoiceId")
                    invoiceId = existingInvoiceId
                }

                if (!alreadySigned) {
                    Log.d(TAG, "signInvoice: firmando invoiceId=$invoiceId...")
                    val signResult = signInvoiceUseCase(invoiceId).getOrThrow()
                    Log.d(TAG, "signInvoice: resultado success=${signResult.success} status=${signResult.status}")
                    if (!signResult.success) {
                        Log.e(TAG, "signInvoice: firma fallida status=${signResult.status}")
                        _state.update { it.copy(isEmitting = false, error = "Error en firma: ${signResult.status}") }
                        return@launch
                    }
                    Log.i(TAG, "signInvoice: OK")
                    _state.update { it.copy(isSigned = true) }
                } else {
                    Log.d(TAG, "signInvoice: omitido (ya firmado)")
                }

                Log.d(TAG, "sendToSunat: enviando invoiceId=$invoiceId...")
                val sunatResult = sendToSunatUseCase(invoiceId).getOrThrow()
                Log.i(TAG, "sendToSunat: OK | isSuccess=${sunatResult.isSuccess} response=$sunatResult")
                _state.update { it.copy(
                    isEmitting = false,
                    showResultScreen = true,
                    sunatResponse = sunatResult
                ) }

                if (sunatResult.isSuccess) {
                    Log.d(TAG, "sendToSunat: borrador limpiado del store")
                    _state.update { it.copy(emittedDraft = draft) }
                    draftStore.clear()
                }

            } catch (e: Exception) {
                Log.e(TAG, "onConfirmEmit: error inesperado", e)
                _state.update { it.copy(isEmitting = false, error = e.toUserFriendlyMessage("Error al emitir el comprobante")) }
            }
        }
    }

    fun onDownloadPdf() {
        val currentState = _state.value
        val draft = currentState.emittedDraft ?: return
        if (currentState.sunatResponse?.isSuccess != true) return

        viewModelScope.launch {
            try {
                val company = getCompanyUseCase().getOrNull()
                val invoiceEntity = draft.toPreviewEntity(
                    serie = currentState.series ?: "",
                    correlative = currentState.correlative?.toLongOrNull() ?: 0L,
                    status = "ACEPTADA"
                )
                val pdfFile = withContext(Dispatchers.IO) {
                    generateInvoicePdf(context, invoiceEntity, company)
                }
                val fileName = "Comprobante_${currentState.documentId ?: "comprobante"}.pdf"
                _navigationEvent.send(InvoicePreviewNavigationEvent.DownloadPdf(pdfFile, fileName))
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al generar PDF") }
            }
        }
    }

    fun onShare() {
        val currentState = _state.value
        val draft = currentState.emittedDraft ?: return
        if (currentState.sunatResponse?.isSuccess != true) return

        viewModelScope.launch {
            try {
                val company = getCompanyUseCase().getOrNull()
                val invoiceEntity = draft.toPreviewEntity(
                    serie = currentState.series ?: "",
                    correlative = currentState.correlative?.toLongOrNull() ?: 0L,
                    status = "ACEPTADA"
                )
                val pdfFile = withContext(Dispatchers.IO) {
                    generateInvoicePdf(context, invoiceEntity, company)
                }
                _navigationEvent.send(InvoicePreviewNavigationEvent.ShareInvoice(
                    companyName = company?.businessName ?: "Mi Empresa",
                    companyRuc = company?.documentNumber ?: "",
                    documentName = if (draft.type == InvoiceType.FACTURA) "Factura" else "Boleta",
                    documentId = currentState.documentId ?: "",
                    customerName = draft.clientBusinessName ?: draft.clientName ?: "Cliente",
                    total = draft.total.toString(),
                    pdfFile = pdfFile
                ))
            } catch (e: Exception) {
                _state.update { it.copy(error = "Error al compartir el comprobante") }
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
                val invoiceEntity = draft.toPreviewEntity(serie ?: if (draft.type == InvoiceType.FACTURA) "F001" else "B001")
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

    // Solo para renderizar el PDF de vista previa. Nunca se persiste ni se envía a SUNAT.
    // correlative = 0L es el sentinel que PdfUtils detecta para mostrar "--------" en lugar del número real.
    // igvAmount asume 18% flat; productos exonerados mostrarán montos aproximados en el PDF.
    // Los valores reales los calcula la Edge Function al emitir.
    private fun Invoice.toPreviewEntity(
        serie: String = if (type == InvoiceType.FACTURA) "F001" else "B001",
        correlative: Long = 0L,
        status: String = "BORRADOR"
    ) = InvoiceEntity(
        id = "",
        companyId = "",
        documentType = if (type == InvoiceType.FACTURA) ElectronicDocumentTypeEntity.FACTURA else ElectronicDocumentTypeEntity.BOLETA,
        serie = serie,
        correlative = correlative,
        total = total.toString(),
        status = status,
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
                igvAmount = (item.subtotal * 0.18).toString(),
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
