package com.highv.ecommerce.domain.coupon.dto

import com.highv.ecommerce.domain.coupon.enumClass.DiscountPolicy
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

data class UpdateCouponRequest(

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val expiredAt: LocalDateTime,

    val discountPolicy: DiscountPolicy,

    @field:Min(value = 1, message = "최소 1이어야 합니다")
    @field:Max(value = 50000, message = "최대 50000을 넘길 수 없습니다")
    val discount : Int,

    @field:Min(value = 1, message = "최소 1이어야 합니다")
    val quantity : Int,
)