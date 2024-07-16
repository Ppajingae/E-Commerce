package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponJpaRepository: JpaRepository<Coupon, Long>{
    fun findByIdOrNull(id: Long): Coupon?
}