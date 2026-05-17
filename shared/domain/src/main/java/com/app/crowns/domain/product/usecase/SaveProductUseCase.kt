package com.app.crowns.domain.product.usecase

import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.product.repository.ProductRepository
import javax.inject.Inject

class SaveProductUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(product: Product): Result<Unit> =
        repository.saveProduct(product)
}
