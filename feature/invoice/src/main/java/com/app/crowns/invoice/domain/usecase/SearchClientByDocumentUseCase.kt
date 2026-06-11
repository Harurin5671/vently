package com.app.crowns.invoice.domain.usecase

import com.app.crowns.domain.company.usecase.SearchCompanyUseCase
import com.app.crowns.domain.customer.usecase.SearchPersonUseCase
import com.app.crowns.network.fold
import javax.inject.Inject

sealed class ClientSearchResult {
    data class PersonFound(val fullName: String) : ClientSearchResult()
    data class CompanyFound(val businessName: String) : ClientSearchResult()
}

class SearchClientByDocumentUseCase @Inject constructor(
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val searchPersonUseCase: SearchPersonUseCase,
) {
    suspend operator fun invoke(documentNumber: String, isRuc: Boolean): Result<ClientSearchResult> =
        if (isRuc) {
            searchCompanyUseCase(documentNumber).fold(
                onSuccess = { Result.success(ClientSearchResult.CompanyFound(it.businessName)) },
                onHttpError = { Result.failure(Exception("No se encontró el RUC o hubo un error")) },
                onNetworkError = { Result.failure(Exception("Sin conexión a internet")) },
            )
        } else {
            searchPersonUseCase(documentNumber).fold(
                onSuccess = { Result.success(ClientSearchResult.PersonFound(it.fullName)) },
                onHttpError = { Result.failure(Exception("No se encontró el DNI o hubo un error")) },
                onNetworkError = { Result.failure(Exception("Sin conexión a internet")) },
            )
        }
}
