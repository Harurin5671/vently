package com.app.crowns.domain.product.usecase

import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.product.repository.ProductRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(productId: String): Result<Product> {
        return repository.getProductById(productId)
    }
}
