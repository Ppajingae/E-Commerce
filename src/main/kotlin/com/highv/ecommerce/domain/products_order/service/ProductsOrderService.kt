package com.highv.ecommerce.domain.products_order.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer_history.entity.BuyerHistory
import com.highv.ecommerce.domain.buyer_history.repository.BuyerHistoryRepository
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_status.entity.OrderStatus
import com.highv.ecommerce.domain.order_status.enumClass.RejectReason
import com.highv.ecommerce.domain.order_status.repository.OrderStatusJpaRepository
import com.highv.ecommerce.domain.products_order.dto.OrderStatusRequest
import com.highv.ecommerce.domain.products_order.entity.ProductsOrder
import com.highv.ecommerce.domain.products_order.enumClass.StatusCode
import com.highv.ecommerce.domain.products_order.repository.ProductsOrderRepository
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ProductsOrderService(
    private val productsOrderRepository: ProductsOrderRepository,
    private val orderStatusRepository: OrderStatusJpaRepository,
    private val itemCartRepository: ItemCartRepository,
    private val buyerHistoryRepository: BuyerHistoryRepository,
){

    @Transactional
    fun requestPayment(userPrincipal: UserPrincipal): DefaultResponse {
        //TODO(카트 아이디 를 조회 해서 물건을 가져 온다 -> List<CartItem>)
        val itemCart = itemCartRepository.findAllByBuyerId(userPrincipal.id)
        val price = itemCart.sumOf { it.price }

        //TODO(만약에 CartItem 에 ProductId 가 Coupon 의 ProductId와 일치할 경우 CartItem 의 가격을 임시로 업데이트)

        val productsOrder = productsOrderRepository.saveAndFlush(
            ProductsOrder(
                statusCode = StatusCode.ORDERED,
                buyerId = userPrincipal.id,
                isPaid = false,
                payDate = LocalDateTime.now(),
                totalPrice = price,
                deliveryStartAt = LocalDateTime.now(),
                deliveryEndAt = LocalDateTime.now(),
                regDate = LocalDateTime.now(),
            )
        )

        itemCart.map { it.paymentUpdate(productsOrder.id!!) }

        buyerHistoryRepository.save(
            BuyerHistory(
                orderId = productsOrder.id!!,
                buyerId = userPrincipal.id
            )
        )

        orderStatusRepository.saveAll(
            itemCart.map {
                OrderStatus(
                    rejectReason = RejectReason.NONE,
                    itemCart = it,
                    productsOrder = productsOrder,
                    shopId = 1L,
                    buyerId = userPrincipal.id
                )
            }

        )


        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : ${productsOrder.id}")
    }

    @Transactional
    fun updateOrderStatus(orderId: Long, orderStatusRequest: OrderStatusRequest, userPrincipal: UserPrincipal): DefaultResponse {

        val order = productsOrderRepository.findByIdOrNull(orderId) ?: throw RuntimeException()

        order.update(orderStatusRequest)

        productsOrderRepository.save(order)

        return DefaultResponse.from("주문 상태 변경이 완료 되었습니다. 변경된 상태 : ${orderStatusRequest.statusCode.name}")
    }



}