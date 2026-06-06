package com.app.crowns.domain.customer.usecase

import com.app.crowns.domain.company.repository.CompanyRepository
import com.app.crowns.domain.customer.model.PersonInfo
import com.app.crowns.network.NetworkResult
import javax.inject.Inject

class SearchPersonUseCase @Inject constructor(
    private val repository: CompanyRepository,
) {
    suspend operator fun invoke(dni: String): NetworkResult<PersonInfo> {
        android.util.Log.d("SEARCH_PERSON_USECASE", "Ejecutando SearchPersonUseCase para DNI: $dni")
        val result = repository.getPersonInfo(dni)
        android.util.Log.d("SEARCH_PERSON_USECASE", "Resultado de SearchPersonUseCase: $result")
        return result
    }
}
