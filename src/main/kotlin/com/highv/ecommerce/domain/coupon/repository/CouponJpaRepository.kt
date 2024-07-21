package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CouponJpaRepository: JpaRepository<Coupon, Long>{

    fun findByIdAndIsDeletedFalse(id: Long): Coupon?

    fun existsByProductId(productId: Long): Boolean

    fun findByIdAndSellerId(id: Long, sellerId: Long): Coupon?


    fun findAllBySellerId(sellerId: Long): List<Coupon>

    @Query("select GET_LOCK(:name, :time)", nativeQuery = true)
    fun getLock(@Param("name")name: String, @Param("time")time: Int):Int

    @Query("SELECT RELEASE_LOCK(:name)", nativeQuery = true)
    fun releaseLock(@Param("name") name: String): Int
}