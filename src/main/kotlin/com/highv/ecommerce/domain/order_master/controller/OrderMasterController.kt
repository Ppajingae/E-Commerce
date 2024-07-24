package com.highv.ecommerce.domain.order_master.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.domain.order_master.dto.OrderStatusRequest
import com.highv.ecommerce.domain.order_master.service.OrderMasterService
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/")
class OrderMasterController(
    private val productsOrderService: OrderMasterService
) {

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/payments")
    fun requestPayment(
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
        couponIdList: List<Long>
    ): ResponseEntity<DefaultResponse>
        = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.requestPayment(userPrincipal.id, couponIdList))


    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/order_status/{orderId}")
    fun updateOrderStatus(
        @PathVariable("orderId") orderId: Long,
        @RequestBody orderStatusRequest: OrderStatusRequest,
        @AuthenticationPrincipal userPrincipal: UserPrincipal,
    ): ResponseEntity<DefaultResponse>
        = ResponseEntity.status(HttpStatus.OK).body(productsOrderService.updateOrderStatus(orderId, orderStatusRequest, userPrincipal.id))





}