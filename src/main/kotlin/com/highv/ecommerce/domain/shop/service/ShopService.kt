package com.highv.ecommerce.domain.shop.service

import com.highv.ecommerce.domain.shop.dto.CreateShopRequest
import com.highv.ecommerce.domain.shop.dto.ShopResponse
import com.highv.ecommerce.domain.shop.entity.Shop
import com.highv.ecommerce.domain.shop.repository.ShopRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ShopService(private val shopRepository: ShopRepository) {

    fun createShop(sellerId: Long, createShopRequest: CreateShopRequest): ShopResponse {
        val shop = Shop(
            sellerId = sellerId,
            name = createShopRequest.name,
            description = createShopRequest.description,
            shopImage = createShopRequest.shopImage,
            rate = 0.0f
        )
        val savedShop = shopRepository.save(shop)
        return ShopResponse.from(savedShop)
    }

    fun getShopById(sellerId: Long): ShopResponse {
        val shop = shopRepository.findByIdOrNull(sellerId) ?: throw RuntimeException("Shop not found")
        return ShopResponse.from(shop)
    }
}