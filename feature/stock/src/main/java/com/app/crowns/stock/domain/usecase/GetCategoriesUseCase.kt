package com.app.crowns.stock.domain.usecase

import com.app.crowns.stock.domain.model.Category
import com.app.crowns.stock.domain.repository.StockRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: StockRepository,
) {
    suspend operator fun invoke(): Result<List<Category>> =
        repository.getCategories()
}