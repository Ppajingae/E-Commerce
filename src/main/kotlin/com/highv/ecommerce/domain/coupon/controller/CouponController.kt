package com.highv.ecommerce.domain.coupon.controller

import com.highv.ecommerce.common.dto.DefaultResponse
import com.highv.ecommerce.common.exception.InvalidCouponRequestException
import com.highv.ecommerce.common.exception.UnauthorizedUserException
import com.highv.ecommerce.domain.coupon.dto.CouponResponse
import com.highv.ecommerce.domain.coupon.dto.CreateCouponRequest
import com.highv.ecommerce.domain.coupon.dto.UpdateCouponRequest
import com.highv.ecommerce.domain.coupon.service.CouponService
import com.highv.ecommerce.infra.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class CouponController(
    private val couponService: CouponService
) {

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/seller/coupon")
    fun createCoupon(
        @Valid @RequestBody couponRequest: CreateCouponRequest,
        bindingResult: BindingResult,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<DefaultResponse> {

        if (bindingResult.hasErrors()) throw InvalidCouponRequestException(
            400,
            bindingResult.fieldError?.defaultMessage.toString()
        )
        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(couponService.createCoupon(couponRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/seller/coupon/{couponId}")
    fun updateCoupon(
        @PathVariable("couponId") couponId: Long,
        @Valid @RequestBody updateCouponRequest: UpdateCouponRequest,
        bindingResult: BindingResult,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?,
    ): ResponseEntity<DefaultResponse> {

        if (bindingResult.hasErrors()) throw InvalidCouponRequestException(
            400,
            bindingResult.fieldError?.defaultMessage.toString()
        )
        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")


        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.updateCoupon(couponId, updateCouponRequest, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/seller/coupon/{couponId}")
    fun deleteCoupon(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.deleteCoupon(couponId, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/coupon/{couponId}")
    fun getSellerCouponById(
        @PathVariable("couponId") couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<CouponResponse> {

        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getSellerCouponById(couponId, userPrincipal))
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/coupon")
    fun getSellerCouponList(
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<List<CouponResponse>> {

        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getSellerCouponList(userPrincipal))
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer/coupon/{couponId}")
    fun getBuyerCouponById(
        @PathVariable("couponId") couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<CouponResponse> {

        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getBuyerCouponById(couponId, userPrincipal))
    }

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/buyer/coupon")
    fun getBuyerCouponList(
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<List<CouponResponse>> {

        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.getBuyerCouponList(userPrincipal))
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/buyer/coupon/{couponId}")
    fun issuedCoupon(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal?
    ): ResponseEntity<DefaultResponse> {

        if (userPrincipal == null) throw UnauthorizedUserException(401, "인증되지 않은 사용자입니다.")

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.issuedCoupon(couponId, userPrincipal))
    }

    // 최후의 보루
    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/apply/{couponId}")
    fun applyCoupon(
        @PathVariable couponId: Long,
        @AuthenticationPrincipal userPrincipal: UserPrincipal
    ): ResponseEntity<DefaultResponse> {

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(couponService.applyCoupon(couponId, userPrincipal.id))
    }
}