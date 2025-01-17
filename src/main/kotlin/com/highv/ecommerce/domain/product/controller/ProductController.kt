package com.highv.ecommerce.domain.product.controller

import com.highv.ecommerce.common.exception.CustomRuntimeException
import com.highv.ecommerce.domain.product.dto.CreateRequest
import com.highv.ecommerce.domain.product.dto.ProductResponse
import com.highv.ecommerce.domain.product.dto.ProductSummaryResponse
import com.highv.ecommerce.domain.product.dto.UpdateProductRequest
import com.highv.ecommerce.domain.product.service.ProductService
import com.highv.ecommerce.infra.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    private val productService: ProductService
) {
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    fun createProduct(
        @AuthenticationPrincipal seller: UserPrincipal,
        @Valid @RequestBody createRequest: CreateRequest,
        bindingResult: BindingResult
    ): ResponseEntity<ProductResponse> {

        if (bindingResult.hasErrors()) {
            throw CustomRuntimeException(400, bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                productService.createProduct(
                    seller.id,
                    createRequest.createProductRequest,
                    createRequest.createProductBackOfficeRequest
                )
            )
    }

    @PatchMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    fun updateProduct(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable("productId") productId: Long,
        @Valid @RequestBody updateProductRequest: UpdateProductRequest,
        bindingResult: BindingResult
    ): ResponseEntity<ProductResponse> {
        if (bindingResult.hasErrors()) {
            throw CustomRuntimeException(400, bindingResult.fieldError?.defaultMessage.toString())
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.updateProduct(seller.id, productId, updateProductRequest))
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    fun deleteProduct(
        @AuthenticationPrincipal seller: UserPrincipal,
        @PathVariable productId: Long
    ): ResponseEntity<Unit> = ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(productService.deleteProduct(seller.id, productId))

    @GetMapping("/{productId}")
    fun getProductById(
        @PathVariable("productId") productId: Long
    ): ResponseEntity<ProductResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductById(productId))

    @GetMapping("/category")
    fun getProductsByCategory(
        categoryId: Long,
        @PageableDefault(size = 10, page = 0) pageable: Pageable
    ): ResponseEntity<Page<ProductSummaryResponse>> = ResponseEntity
        .status(HttpStatus.OK)
        .body(productService.getProductsByCategory(categoryId, pageable))
}