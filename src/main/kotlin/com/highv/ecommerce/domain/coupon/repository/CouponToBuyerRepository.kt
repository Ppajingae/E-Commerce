package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.CouponToBuyer

interface CouponToBuyerRepository {

    fun findAllProductIdWithBuyerId(buyerId: Long): List<Long>

    fun existsByCouponIdAndBuyerId(couponId: Long, buyerId: Long): Boolean

    fun save(coupon: CouponToBuyer): CouponToBuyer

    fun findByProductIdAndBuyerId(productId: Long, buyerId: Long): CouponToBuyer?

    fun findAllByCouponIdAndBuyerIdAndIsUsedFalse(couponIdList: List<Long>, buyerId: Long): List<CouponToBuyer>

    fun deleteAll(couponToBuyerList: List<CouponToBuyer>)

    fun findByCouponIdAndBuyerIdAndIsUsedFalse(couponId: Long, buyerId: Long): CouponToBuyer?

    fun findAllByCouponIdAndBuyerIdAndIsUsedTrue(coupons: List<Long>, buyerId: Long):List<CouponToBuyer>
}