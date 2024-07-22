package com.highv.ecommerce.item_cart

import com.highv.ecommerce.domain.item_cart.entity.ItemCart
import com.highv.ecommerce.domain.product.entity.Product
import com.highv.ecommerce.domain.shop.entity.Shop
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ItemCartEntityTest : AnnotationSpec() {

    @Test
    fun `상품 수량을 변경할 때 수량이 1개 보다 적으면 예외가 발생한다`() {
        val shop = Shop(
            sellerId = 1L,
            name = "testName",
            description = "testDescription",
            shopImage = "testImage",
            rate = 0.0f
        )

        val product = Product(
            name = "Test Product",
            description = "Test Description",
            productImage = "image.jpg",
            favorite = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = LocalDateTime.now(),
            isDeleted = false,
            shop = shop,
            categoryId = 1L
        ).apply { id = 1L }

        val cart = ItemCart(
            product = product,
            productName = "testName",
            price = 3000,
            quantity = 1,
            buyerId = 1L
        ).apply { id = 1L }

        shouldThrow<RuntimeException> {
            cart.updateQuantity(0)
        }.let {
            it.message shouldBe "물품의 수량이 0보다 작거나 같을 수 없습니다."
        }
    }

    @Test
    fun `상품 수량을 변경할 때 1개보다 많으면 변경된다`() {

        val shop = Shop(
            sellerId = 1L,
            name = "testName",
            description = "testDescription",
            shopImage = "testImage",
            rate = 0.0f
        )

        val product = Product(
            name = "Test Product",
            description = "Test Description",
            productImage = "image.jpg",
            favorite = 0,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
            isSoldOut = false,
            deletedAt = LocalDateTime.now(),
            isDeleted = false,
            shop = shop,
            categoryId = 1L
        ).apply { id = 1L }

        val cart = ItemCart(
            product = product,
            productName = "testName",
            price = 3000,
            quantity = 1,
            buyerId = 1L
        ).apply { id = 1L }

        cart.updateQuantity(6)
    }
}