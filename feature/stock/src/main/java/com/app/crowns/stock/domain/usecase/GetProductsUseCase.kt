package com.app.crowns.stock.domain.usecase

import com.app.crowns.stock.domain.model.Product
import com.app.crowns.stock.domain.repository.StockRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: StockRepository,
) {
    suspend operator fun invoke(): Result<List<Product>> =
        repository.getProducts()
}