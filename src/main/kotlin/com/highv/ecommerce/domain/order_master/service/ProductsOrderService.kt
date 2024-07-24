package com.highv.ecommerce.domain.order_master.service

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.buyer.repository.BuyerRepository
import com.highv.ecommerce.domain.buyer_history.entity.BuyerHistory
import com.highv.ecommerce.domain.buyer_history.repository.BuyerHistoryRepository
import com.highv.ecommerce.domain.item_cart.repository.ItemCartRepository
import com.highv.ecommerce.domain.order_details.entity.OrderDetails
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.repository.OrderStatusJpaRepository
import com.highv.ecommerce.domain.order_master.dto.CouponRequest
import com.highv.ecommerce.domain.order_master.dto.OrderStatusRequest
import com.highv.ecommerce.domain.order_master.entity.OrderMaster
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import com.highv.ecommerce.domain.order_master.repository.ProductsOrderRepository
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
    private val salesHistoryRepository: SalesHistoryRepository,
    private val buyerRepository: BuyerRepository,
    ){

    @Transactional
    fun requestPayment(buyerId: Long, couponRequest: CouponRequest): DefaultResponse {

        //TODO(카트 아이디 를 조회 해서 물건을 가져 온다 -> List<CartItem>)

        val itemCart = itemCartRepository.findAllByBuyerIdAndIsDeletedFalse(buyerId)

        val totalPrice = productsOrderRepository.discountTotalPriceList(buyerId, couponRequest.couponIdList)



        val buyer = buyerRepository.findByIdOrNull(buyerId)!!

        //TODO(만약에 CartItem 에 ProductId 가 Coupon 의 ProductId와 일치할 경우 CartItem 의 가격을 임시로 업데이트)

        val productsOrder = productsOrderRepository.saveAndFlush(
            OrderMaster(
                statusCode = OrderStatus.ORDERED,
                buyerId = buyerId,
                isPaid = false,
                payDate = LocalDateTime.now(),
                totalPrice = totalPrice,
                deliveryStartAt = LocalDateTime.now(),
                deliveryEndAt = LocalDateTime.now(),
                regDate = LocalDateTime.now(),
            )
        )

        itemCart.map { it.paymentUpdate(productsOrder.id!!) }

        buyerHistoryRepository.save(
            BuyerHistory(
                orderId = productsOrder.id!!,
                buyerId = buyerId
            )
        )

        salesHistoryRepository.save(
            SalesHistory(
                sellerId = itemCart[0].product.shop.sellerId,
                price = totalPrice,
                regDt = LocalDateTime.now(),
                buyerName = buyer.nickname,
                orderId = productsOrder.id,
            )
        )

        orderStatusRepository.saveAll(
            itemCart.map {
                OrderDetails(
                    orderPendingReason = ComplainStatus.NONE,
                    itemCart = it,
                    productsOrder = productsOrder,
                    shopId = it.product.shop.id!!,
                    buyerId = buyerId
                )
            }

        )


        return DefaultResponse.from("주문이 완료 되었습니다, 주문 번호 : ${productsOrder.id}")
    }

    @Transactional
    fun updateOrderStatus(orderId: Long, orderStatusRequest: OrderStatusRequest, sellerId: Long): DefaultResponse {

        val order = productsOrderRepository.findByIdOrNull(orderId) ?: throw RuntimeException()

        order.update(orderStatusRequest)

        productsOrderRepository.save(order)

        return DefaultResponse.from("주문 상태 변경이 완료 되었습니다. 변경된 상태 : ${orderStatusRequest.statusCode.name}")
    }



}