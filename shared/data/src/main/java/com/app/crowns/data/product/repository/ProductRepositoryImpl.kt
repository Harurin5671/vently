package com.app.crowns.data.product.repository

import com.app.crowns.data.product.datasource.ProductRemoteDataSource
import com.app.crowns.data.product.mapper.toDomain
import com.app.crowns.data.product.mapper.toEntity
import com.app.crowns.domain.company.repository.CompanyRepository
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.product.repository.ProductRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val companyRepository: CompanyRepository,
) : ProductRepository {

    private val _productChanges = MutableSharedFlow<Unit>(replay = 1)
    override val productChanges = _productChanges.asSharedFlow()

    override suspend fun notifyProductChanged() {
        _productChanges.emit(Unit)
    }

    private var cachedCompanyId: String? = null

    private suspend fun getCompanyId(): String {
        cachedCompanyId?.let { return it }
        val company = companyRepository.getCompany().getOrNull()
            ?: throw Exception("Empresa no encontrada")
        return company.id.also { cachedCompanyId = it }
    }

    override suspend fun getProducts(): Result<List<Product>> = runCatching {
        val companyId = getCompanyId()

        val categories = remoteDataSource.getCategories(companyId).getOrDefault(emptyList())
        remoteDataSource.getProducts(companyId).getOrThrow().map { entity ->
            val category = categories.find { it.id == entity.categoryId }?.toDomain()
            entity.toDomain(category)
        }
    }

    override suspend fun getProductById(productId: String): Result<Product> = runCatching {
        val companyId = getCompanyId()
        val categories = remoteDataSource.getCategories(companyId).getOrDefault(emptyList())
        val productEntity = remoteDataSource.getProductById(productId).getOrThrow()
        val category = categories.find { it.id == productEntity.categoryId }?.toDomain()
        productEntity.toDomain(category)
    }

    override suspend fun saveProduct(product: Product, imageBytes: ByteArray?): Result<Unit> = runCatching {
        val companyId = getCompanyId()

        val productId: String
        if (product.id != null) {
            // Actualizar producto existente
            remoteDataSource.updateProduct(product.toEntity(companyId)).getOrThrow()
            productId = product.id!!
        } else {
            // Insertar nuevo producto
            val saved = remoteDataSource.saveProduct(product.toEntity(companyId)).getOrThrow()
            productId = saved.id ?: throw Exception("Error al obtener ID del producto guardado")
        }

        // Si hay nueva imagen, subirla y actualizar la URL
        if (imageBytes != null) {
            val timestamp = System.currentTimeMillis()
            val imagePath = "$companyId/${productId}_$timestamp.jpg"
            val imageUrl = remoteDataSource.uploadProductImage(imageBytes, imagePath).getOrThrow()
            remoteDataSource.updateProduct(product.toEntity(companyId).copy(id = productId, imageUrl = imageUrl)).getOrThrow()
        }

        notifyProductChanged()
    }

    override suspend fun getCategories(): Result<List<Category>> = runCatching {
        val companyId = getCompanyId()

        remoteDataSource.getCategories(companyId).getOrThrow().map { it.toDomain() }
    }

    override suspend fun saveCategory(category: Category): Result<Unit> = runCatching {
        val companyId = getCompanyId()
        remoteDataSource.saveCategory(category.toEntity(companyId)).getOrThrow()
        notifyProductChanged()
    }

    override suspend fun updateCategory(category: Category): Result<Unit> = runCatching {
        val companyId = getCompanyId()
        remoteDataSource.updateCategory(category.toEntity(companyId)).getOrThrow()
        notifyProductChanged()
    }
}
