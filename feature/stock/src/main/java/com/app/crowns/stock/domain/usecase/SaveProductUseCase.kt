package com.app.crowns.stock.domain.usecase

import com.app.crowns.stock.domain.model.Product
import com.app.crowns.stock.domain.repository.StockRepository
import javax.inject.Inject

class SaveProductUseCase @Inject constructor(
    private val repository: StockRepository,
) {
    suspend operator fun invoke(product: Product): Result<Unit> =
        repository.saveProduct(product)
}