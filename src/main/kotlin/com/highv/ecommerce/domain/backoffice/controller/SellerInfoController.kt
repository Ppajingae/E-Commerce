package com.highv.ecommerce.domain.backoffice.controller

import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdatePasswordRequest
import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateSellerRequest
import com.highv.ecommerce.domain.backoffice.dto.sellerInfo.UpdateShopRequest
import com.highv.ecommerce.domain.backoffice.service.SellerInfoService
import com.highv.ecommerce.domain.seller.dto.SellerResponse
import com.highv.ecommerce.domain.seller.shop.dto.ShopResponse
import com.highv.ecommerce.infra.security.UserPrincipal
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/sellerInfo")
class SellerInfoController(
    private val sellerInfoService: SellerInfoService
) {
    @PatchMapping("/myShopInfo")
    @PreAuthorize("hasRole('SELLER')")
    fun updateShopInfo(
        @AuthenticationPrincipal seller: UserPrincipal,
        @RequestBody updateShopRequest: UpdateShopRequest,
    ): ResponseEntity<ShopResponse> = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(sellerInfoService.updateShopInfo(seller.id, updateShopRequest))

    @PatchMapping("/myInfo")
    @PreAuthorize("hasRole('SELLER')")
    fun updateSellerInfo(
        @AuthenticationPrincipal seller: UserPrincipal,
        @RequestBody updateSellerRequest: UpdateSellerRequest
    ): ResponseEntity<SellerResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(sellerInfoService.updateSellerInfo(seller.id, updateSellerRequest))

    @PatchMapping("/myInfo/changePassword")
    @PreAuthorize("hasRole('SELLER')")
    fun changePassword(
        @AuthenticationPrincipal seller: UserPrincipal,
        @RequestBody updatePasswordRequest: UpdatePasswordRequest
    ): ResponseEntity<String> = ResponseEntity
        .status(HttpStatus.OK)
        .body(sellerInfoService.changePassword(seller.id, updatePasswordRequest))

    @GetMapping("/myInfo")
    fun getSellerInfo(
        @AuthenticationPrincipal seller: UserPrincipal
    ): ResponseEntity<SellerResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(sellerInfoService.getSellerInfo(seller.id))

    @GetMapping("/myShopInfo")
    fun getShopInfo(
        @AuthenticationPrincipal seller: UserPrincipal
    ): ResponseEntity<ShopResponse> = ResponseEntity
        .status(HttpStatus.OK)
        .body(sellerInfoService.getShopInfo(seller.id))
}