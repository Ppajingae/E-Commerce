package com.highv.ecommerce

import com.highv.ecommerce.domain.item_cart.dto.request.AddItemIntoCartRequest
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.item_cart.service.ItemCartService
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.product.repository.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class ItemCartServiceTest : BehaviorSpec() {

    init {

        val itemCartRepository = mockk<ItemCartRepository>(relaxed = true)
        val productRepository = mockk<ProductRepository>(relaxed = true)

        val itemCartService: ItemCartService = ItemCartService(itemCartRepository, productRepository)

        afterContainer {
            clearAllMocks()
        }

        Given("상품을 장바구니에 담을 때") {

            val product = Product(
                name = "Test Product",
                description = "Test Description",
                price = 1000,
                productImage = "image.jpg",
                favorite = 0,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                quantity = 10,
                isSoldOut = false,
                deletedAt = LocalDateTime.now(),
                isDeleted = false,
                shopId = 1L,
                categoryId = 1L
            ).apply { id = 1L }

            val buyerId = 1L

            When("상품 수량이 1개 이상이면") {
                val request: AddItemIntoCartRequest = AddItemIntoCartRequest(quantity = 1)

                every { productRepository.findByIdOrNull(any()) } returns product

                Then("장바구니에 추가된다.") {
                    itemCartService.addItemIntoCart(product.id!!, request, buyerId)

                    verify(exactly = 1) { productRepository.findByIdOrNull(any()) }
                }

            }

            When("상품 수량이 1개보다 적으면") {
                val request: AddItemIntoCartRequest = AddItemIntoCartRequest(quantity = 0)

                every { productRepository.findByIdOrNull(any()) } returns product

                Then("상품 개수 예외가 발생한다.") {
                    shouldThrow<RuntimeException> {
                        itemCartService.addItemIntoCart(product.id!!, request, buyerId)
                    }.let {
                        it.message shouldBe "상품의 개수가 1개보다 적을 수 없습니다."
                    }
                }
            }

        }
    }
}