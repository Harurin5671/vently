package com.app.crowns.domain.product.usecase

import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.product.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(): Result<List<Product>> =
        repository.getProducts()
}
