package com.app.crowns.domain.customer.usecase

import com.app.crowns.domain.company.repository.CompanyRepository
import com.app.crowns.domain.customer.model.PersonInfo
import com.app.crowns.network.NetworkResult
import javax.inject.Inject

class SearchPersonUseCase @Inject constructor(
    private val repository: CompanyRepository,
) {
    suspend operator fun invoke(dni: String): NetworkResult<PersonInfo> =
        repository.getPersonInfo(dni)
}
