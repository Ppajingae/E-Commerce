package com.highv.ecommerce.domain.buyer.service

import com.highv.ecommerce.domain.buyer.dto.request.BuyerOrderStatusUpdateRequest
import com.highv.ecommerce.domain.buyer.dto.request.CreateBuyerRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerImageRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerPasswordRequest
import com.highv.ecommerce.domain.buyer.dto.request.UpdateBuyerProfileRequest
import com.highv.ecommerce.domain.buyer.dto.response.BuyerHistoryProductResponse
import com.highv.ecommerce.domain.buyer.dto.response.BuyerOrderResponse
import com.highv.ecommerce.domain.buyer.dto.response.BuyerResponse
import com.highv.ecommerce.domain.buyer.entity.Buyer
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.buyer_history.repository.BuyerHistoryRepository
import com.highv.ecommerce.domain.order_status.entity.OrderStatus
import com.highv.ecommerce.domain.order_status.enumClass.OrderPendingReason
import com.highv.ecommerce.domain.order_status.repository.OrderStatusJpaRepository
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import com.highv.ecommerce.domain.products_order.enumClass.OrderStatusType
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import com.highv.ecommerce.s3.config.S3Manager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

@Service
class BuyerService(
    private val buyerRepository: BuyerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val buyerHistoryRepository: BuyerHistoryRepository,
    private val orderStatusJpaRepository: OrderStatusJpaRepository,
    private val productsOrderRepository: ProductsOrderRepository,
    private val s3Manager: S3Manager
) {

    fun signUp(request: CreateBuyerRequest,multipartFile: MultipartFile): BuyerResponse {

        if (buyerRepository.existsByEmail(request.email)) {
            throw RuntimeException("이미 존재하는 이메일입니다. 가입할 수 없습니다.")
        }

        val buyer = Buyer(
            email = request.email,
            nickname = request.nickname,
            password = passwordEncoder.encode(request.password),
            profileImage = request.profileImage,
            phoneNumber = request.phoneNumber,
            address = request.address,
            providerName = null,
            providerId = null
        )
        val fileUrl = s3Manager.uploadFile(multipartFile) // S3Manager를 통해 파일 업로드
        buyer.profileImage = fileUrl // Buyer 객체에 프로필 이미지 URL 저장


        val savedBuyer = buyerRepository.save(buyer)

        return BuyerResponse.from(savedBuyer)
    }

    @Transactional
    fun changePassword(request: UpdateBuyerPasswordRequest, userId: Long) {

        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw RuntimeException("사용자가 존재하지 않습니다.")


        if (buyer.providerName != null) {
            throw RuntimeException("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.")
        }

        if (!passwordEncoder.matches(request.currentPassword, buyer.password)) {
            throw RuntimeException("비밀번호가 일치하지 않습니다.")
        }

        if (request.newPassword != request.confirmNewPassword) {
            throw RuntimeException("변경할 비밀번호와 확인 비밀번호가 다릅니다.")
        }

        if (passwordEncoder.matches(request.newPassword, buyer.password)) {
            throw RuntimeException("현재 비밀번호와 수정할 비밀번호가 같습니다.")
        }

        buyer.password = passwordEncoder.encode(request.newPassword)
    }

    @Transactional
    fun changeProfileImage(request: UpdateBuyerImageRequest, userId: Long,multipartFile: MultipartFile) {

        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw RuntimeException("사용자가 존재하지 않습니다.")

        s3Manager.uploadFile(multipartFile)
        buyer.profileImage = request.imageUrl

        buyerRepository.save(buyer)
    }

    @Transactional
    fun changeProfile(request: UpdateBuyerProfileRequest, userId: Long): BuyerResponse {

        val buyer = buyerRepository.findByIdOrNull(userId) ?: throw RuntimeException("사용자가 존재하지 않습니다.")

        if (buyer.providerName != null) {
            buyer.address = request.address
            buyer.phoneNumber = request.phoneNumber
        } else {
            buyer.nickname = request.nickname
            buyer.address = request.address
            buyer.phoneNumber = request.phoneNumber
        }

        val saveBuyer = buyerRepository.save(buyer)

        return BuyerResponse.from(saveBuyer)
    }

    // 추후 리팩토링 때 내부 로직에서 orderService의 로직 이용 가능한 것 있으면 사용
    fun getOrders(buyerId: Long): List<BuyerOrderResponse> {
        /* // 주문 내역 전체 불러오기
        * 1. buyerId를 이용해서 주문 내역 전부 가져오기
        * 2. 주문 내역에 있는 order_id를 이용해서 status와 장바구니 내역 가져오기
        * 3. 장바구니 내역과 잘 조합해서 반환하기
        * */

        // val buyerHistories: List<BuyerHistory> = buyerHistoryRepository.findAllByBuyerId(buyerId)

        val orderStatuses: List<OrderStatus> = orderStatusJpaRepository.findAllByBuyerId(buyerId)

        // val orderGroups: MutableMap<Long, MutableList<OrderStatus>> = mutableMapOf()
        //
        // orderStatuses.forEach {
        //
        //     if (!orderGroups.containsKey(it.productsOrder.id!!)) {
        //         orderGroups[it.productsOrder.id] = mutableListOf<OrderStatus>()
        //     }
        //     orderGroups[it.productsOrder.id]!!.add(it)
        // }

        val orderMap: MutableMap<Long, ProductsOrder> = mutableMapOf()
        orderStatuses.forEach {
            if (!orderMap.containsKey(it.productsOrder.id!!)) {
                orderMap[it.productsOrder.id] = it.productsOrder
            }
        }

        val orderGroups: MutableMap<Long, MutableList<BuyerHistoryProductResponse>> = mutableMapOf()

        orderStatuses.forEach {

            if (!orderGroups.containsKey(it.productsOrder.id!!)) {
                orderGroups[it.productsOrder.id] = mutableListOf<BuyerHistoryProductResponse>()
            }
            orderGroups[it.productsOrder.id]!!.add(
                BuyerHistoryProductResponse.from(
                    cart = it.itemCart,
                    orderPendingReason = it.orderPendingReason,
                    orderStatusId = it.id!!
                )
            )
        }

        val buyerOrderResponse: List<BuyerOrderResponse> = orderMap.map {
            BuyerOrderResponse.from(
                productsOrder = it.value,
                products = orderGroups[it.key]!!,
            )
        }

        return buyerOrderResponse.sortedByDescending { it.orderRegisterDate }
    }

    @Transactional
    // 주문 내역에서 상품(상품이 포함된 주문목록 전체) 교환 및 환불 신청하기
    fun updateStatus(buyerId: Long, orderId: Long, request: BuyerOrderStatusUpdateRequest): BuyerOrderResponse {
        /*
        * 1. orderId와 userId로 주문 정보 목록 불러오기
        * 2. request의 상태로 상태 변경 후 이유 넣기
        * 3. productsOrder의 status를 Pending으로 변경하기
        * 4. 변경된 내용 반환
        * */

        val productsOrder: ProductsOrder =
            productsOrderRepository.findByIdOrNull(orderId) ?: throw RuntimeException("수정할 주문 내역이 없습니다.")

        val orderStatuses: List<OrderStatus> =
            orderStatusJpaRepository.findAllByBuyerIdAndProductsOrderId(buyerId, orderId)

        val orderPendingReason: OrderPendingReason = when (request.status) {
            OrderStatusType.EXCHANGE -> OrderPendingReason.EXCHANGE_REQUESTED
            OrderStatusType.REFUND -> OrderPendingReason.REFUND_REQUESTED
        }
        val now: LocalDateTime = LocalDateTime.now()

        // 이것보단 한방 쿼리가 좋을 거라 생각 됨
        orderStatuses.forEach {
            it.orderPendingReason = orderPendingReason
            it.buyerDescription = request.reason
            it.buyerDateTime = now
        }

        productsOrder.apply {
            statusCode = StatusCode.PENDING
        }

        // 위에랑 어짜피 같은 것임 지워도 되지 않을까?
        val savedOrderStatuses = orderStatusJpaRepository.saveAll(orderStatuses)
        val savedProductsOrder = productsOrderRepository.save(productsOrder)

        val buyerHistoryProductResponses: List<BuyerHistoryProductResponse> = savedOrderStatuses.map {
            BuyerHistoryProductResponse.from(
                cart = it.itemCart,
                orderPendingReason = orderPendingReason,
                it.id!!
            )
        }

        return BuyerOrderResponse(
            productsOrderId = savedProductsOrder.id!!,
            orderRegisterDate = savedProductsOrder.regDate,
            orderStatus = savedProductsOrder.statusCode,
            productsOrders = buyerHistoryProductResponses
        )
    }
}
