package com.highv.ecommerce.domain.order_details.service

import com.highv.ecommerce.domain.order_details.dto.BuyerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.dto.OrderStatusResponse
import com.highv.ecommerce.domain.order_details.dto.SellerOrderStatusRequest
import com.highv.ecommerce.domain.order_details.enumClass.ComplainStatus
import com.highv.ecommerce.domain.order_details.enumClass.ComplainType
import com.highv.ecommerce.domain.order_details.repository.OrderDetailsRepository
import com.highv.ecommerce.domain.order_master.dto.ProductsOrderResponse
import com.highv.ecommerce.domain.order_details.enumClass.OrderStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderDetailsService(
    private val orderDetailsRepository: OrderDetailsRepository,
){

    @Transactional
    fun buyerRequestComplain(buyerOrderStatusRequest: BuyerOrderStatusRequest, buyerId: Long, shopId: Long, orderId: Long): OrderStatusResponse {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterId(buyerOrderStatusRequest.shopId, buyerId)

        orderDetails.map {
            it.buyerUpdate(OrderStatus.PENDING, buyerOrderStatusRequest)
        }

        orderDetailsRepository.saveAll(orderDetails)

        return OrderStatusResponse.from(buyerOrderStatusRequest.complainType ,"요청 완료 되었습니다")
    }

    fun getBuyerOrderDetails(buyerId: Long): List<ProductsOrderResponse> {

        val orderDetails = orderDetailsRepository.findAllByBuyerId(buyerId)

        return orderDetails.map { ProductsOrderResponse.from(it) }
    }

    @Transactional
    fun requestComplainReject(sellerOrderStatusRequest: SellerOrderStatusRequest, shopId: Long, orderId: Long): OrderStatusResponse {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(shopId, orderId, sellerOrderStatusRequest.buyerId)
        val complainType =
            if(orderDetails[0].complainStatus == ComplainStatus.REFUND_REQUESTED) ComplainType.REFUND
            else ComplainType.EXCHANGE

        orderDetails.map {
            it.sellerUpdate(OrderStatus.ORDERED, sellerOrderStatusRequest, orderDetails[0].complainStatus)
        }

        orderDetailsRepository.saveAll(orderDetails)

        return OrderStatusResponse.from(complainType,"전체 요청 거절 완료 되었습니다")


    }

    fun getSellerOrderDetailsAll(shopId: Long, sellerId: Long): List<ProductsOrderResponse> {

        val orderDetails = orderDetailsRepository.findAllByShopId(shopId)

        return orderDetails.map { ProductsOrderResponse.from(it) }
    }

    fun getSellerOrderDetailsBuyer(shopId: Long,orderId: Long, buyerId: Long): List<ProductsOrderResponse> {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(shopId, orderId, buyerId)

        return orderDetails.map { ProductsOrderResponse.from(it) }
    }

    @Transactional
    fun requestComplainAccept(shopId: Long, orderId: Long, sellerOrderStatusRequest: SellerOrderStatusRequest): OrderStatusResponse {

        val orderDetails = orderDetailsRepository.findAllByShopIdAndOrderMasterIdAndBuyerId(shopId, orderId, sellerOrderStatusRequest.buyerId)
        val complainType =
            if(orderDetails[0].complainStatus == ComplainStatus.REFUND_REQUESTED) ComplainType.REFUND
            else ComplainType.EXCHANGE
        when (orderDetails[0].complainStatus) {
            ComplainStatus.REFUND_REQUESTED -> {
                orderDetails.map {
                    it.sellerUpdate(OrderStatus.ORDER_CANCELED, sellerOrderStatusRequest, ComplainStatus.REFUNDED)
                }
            }
            ComplainStatus.EXCHANGE_REQUESTED -> {
                orderDetails.map {
                    it.sellerUpdate(OrderStatus.PRODUCT_PREPARING, sellerOrderStatusRequest, ComplainStatus.EXCHANGED)
                }
            }
            else -> throw RuntimeException("구매자가 환불 및 교환 요청을 하지 않았 거나 요청 처리가 완료 되었습니다")
        }


        orderDetailsRepository.saveAll(orderDetails)

        return OrderStatusResponse.from(complainType,"전체 요청 승인 완료 되었습니다")


    }


}