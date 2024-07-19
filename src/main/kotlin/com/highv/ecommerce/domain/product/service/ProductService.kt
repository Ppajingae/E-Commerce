package com.highv.ecommerce.domain.product.service

import com.highv.ecommerce.domain.product.dto.CreateProductRequest
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProductService(
    private val productRepository: ProductRepository
) {
    fun createProduct(sellerId: Long, productRequest: CreateProductRequest): ProductResponse {
        val product = Product(
            name = productRequest.name,
            description = productRequest.description,
            productImage = productRequest.productImage,
            favorite = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = LocalDateTime.now(),
            isDeleted = false,
            shopId = sellerId,
            categoryId = productRequest.categoryId
        )
        val savedProduct = productRepository.save(product)
        return ProductResponse.from(savedProduct)
    }

    fun updateProduct(sellerId: Long, productId: Long, updateProductRequest: UpdateProductRequest): ProductResponse {
        val product = productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")
        if (product.shopId != sellerId) throw RuntimeException("No Authority")
        product.apply {
            name = updateProductRequest.name
            description = updateProductRequest.description
            productImage = updateProductRequest.productImage
            updatedAt = LocalDateTime.now()
            isSoldOut = updateProductRequest.isSoldOut
            categoryId = updateProductRequest.categoryId
        }
        val updatedProduct = productRepository.save(product)
        return ProductResponse.from(updatedProduct)
    }

    fun deleteProduct(sellerId: Long, productId: Long) {
        val product = productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")
        if (product.shopId != sellerId) throw RuntimeException("No Authority")
        product.apply {
            isDeleted = true
            deletedAt = LocalDateTime.now()
        }
        productRepository.save(product)
    }

    fun getProductById(productId: Long): ProductResponse {
        val product = productRepository.findByIdOrNull(productId) ?: throw RuntimeException("Product not found")
        return ProductResponse.from(product)
    }

    fun getAllProducts(pageable: Pageable): Page<ProductResponse> {
        val products = productRepository.findAllPaginated(pageable)
        return products.map { ProductResponse.from(it) }
    }

    fun getProductsByCategory(categoryId: Long, pageable: Pageable): Page<ProductResponse> {
        val products = productRepository.findByCategoryPaginated(categoryId, pageable)
        return products.map { ProductResponse.from(it) }
    }

    fun searchProduct(keyword: String, pageable: Pageable): Page<ProductResponse> {
        val products = productRepository.searchByKeywordPaginated(keyword, pageable)
        if (products.hasContent()) {
            return products.map { ProductResponse.from(it) }
        }
        return Page.empty(pageable)
    }
}