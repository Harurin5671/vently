package com.app.crowns.domain.product.usecase

import com.app.crowns.domain.product.entities.IgvTypeEntity
import com.app.crowns.domain.product.repository.IgvTypeRepository
import javax.inject.Inject

class GetIgvTypesUseCase @Inject constructor(
    private val repository: IgvTypeRepository,
) {
    suspend operator fun invoke(): Result<List<IgvTypeEntity>> =
        repository.getIgvType()
}
