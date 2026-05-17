package com.app.crowns.domain.product.usecase

import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.repository.ProductRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: ProductRepository,
) {
    suspend operator fun invoke(): Result<List<Category>> =
        repository.getCategories()
}
