package com.highv.ecommerce.domain.backoffice.admin.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.backoffice.admin.dto.BlackListResponse
import com.highv.ecommerce.domain.backoffice.admin.dto.CreateBlackListRequest
import com.highv.ecommerce.domain.backoffice.admin.entity.BlackList
import com.highv.ecommerce.domain.backoffice.admin.repository.BlackListRepository
import com.highv.ecommerce.domain.product.repository.ProductRepository
import com.highv.ecommerce.domain.seller.repository.SellerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val sellerRepository: SellerRepository,
    private val productRepository: ProductRepository,
    // private val buyerRepository: BuyerRepository,
    private val blackListRepository: BlackListRepository
) {
    //판매자 제재 로직 구현
    fun sanctionSeller(sellerId: Long): DefaultResponse {
        val seller = sellerRepository.findByIdOrNull(sellerId)
            ?: throw RuntimeException("Seller id $sellerId not found")
        sellerRepository.save(seller)
        return DefaultResponse("판매자 제재 완료")
    }

    // 상품 제재 로직 구현
    fun sanctionProduct(productId: Long): DefaultResponse {
        val product = productRepository.findByIdOrNull(productId)
            ?: throw RuntimeException("Product id $productId not found")
        productRepository.save(product)
        return DefaultResponse("상품 제재 완료")
    }

    /*    //구매자 제재 로직 구현 (미구현)
        fun sanctionBuyer(buyerId: Long): DefaultResponse {
            val buyer = buyerRepository.findByIdOrNull(buyerId)
                ?: throw RuntimeException("Buyer id $buyerId not found")
            buyerRepository.save(buyer)
            return DefaultResponse("구매자 제재 완료")
        }*/

    // 블랙리스트 생성 로직 구현
    @Transactional
    fun createBlackList(request: CreateBlackListRequest): DefaultResponse {
        val existingBlackList = blackListRepository.findByEmail(request.email)
        if (existingBlackList != null) {
            throw RuntimeException("Email already exists in black list")
        }
        val blackList = BlackList(
            nickname = "N/A",
            email = request.email
        )
        blackListRepository.save(blackList)
        return DefaultResponse("블랙리스트 생성 완료")
    }

    // 블랙리스트 조회 로직 구현
    fun getBlackLists(): List<BlackListResponse> {
        return blackListRepository.findAll().map {
            BlackListResponse(it.nickname, it.email)
        }
    }

    // 블랙리스트 단건 조회 로직 구현
    fun getBlackList(blackListId: Long): BlackListResponse {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw RuntimeException("BlackList not found")
        return BlackListResponse(blackList.nickname, blackList.email)
    }

    // 블랙리스트 삭제 로직 구현
    fun deleteBlackList(blackListId: Long): DefaultResponse {
        val blackList = blackListRepository.findByIdOrNull(blackListId)
            ?: throw RuntimeException("BlackList id $blackListId not found")
        blackListRepository.delete(blackList)
        return DefaultResponse("블랙리스트 삭제 완료")
    }
}
