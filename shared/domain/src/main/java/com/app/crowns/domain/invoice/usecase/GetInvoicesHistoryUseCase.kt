package com.app.crowns.domain.invoice.usecase

import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.repository.InvoiceRepository
import com.app.crowns.domain.company.repository.CompanyRepository
import java.time.YearMonth
import javax.inject.Inject

class GetInvoicesHistoryUseCase @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val companyRepository: CompanyRepository,
) {
    companion object {
        const val PAGE_SIZE = 20
    }

    suspend operator fun invoke(yearMonth: YearMonth, offset: Int = 0): Result<List<InvoiceEntity>> = runCatching {
        val company = companyRepository.getCompany().getOrThrow()
        val companyId = company?.id ?: throw Exception("Compañía no encontrada")
        val startDate = yearMonth.atDay(1).toString()
        val endDate = yearMonth.atEndOfMonth().toString()
        invoiceRepository.getInvoicesHistory(companyId, startDate, endDate, offset, PAGE_SIZE).getOrThrow()
    }
}
