package com.app.crowns.domain.invoice.usecase

import android.util.Log
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.repository.InvoiceRepository
import com.app.crowns.domain.company.repository.CompanyRepository
import javax.inject.Inject

class GetInvoicesHistoryUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val companyRepository: CompanyRepository,
) {
    suspend operator fun invoke(): Result<List<InvoiceEntity>> = runCatching {
        Log.d("INVOICE_USECASE", "Obteniendo compañía para historial")
        val company = companyRepository.getCompany().getOrThrow()
        val companyId = company?.id ?: throw Exception("Compañía no encontrada")
        Log.d("INVOICE_USECASE", "Compañía encontrada: ${company.businessName} (ID: $companyId)")
        
        val result = invoiceRepository.getInvoicesHistory(companyId).getOrThrow()
        Log.d("INVOICE_USECASE", "Historial recuperado exitosamente. Items: ${result.size}")
        result
    }.onFailure {
        Log.e("INVOICE_USECASE", "Error en GetInvoicesHistoryUseCase", it)
    }
}
