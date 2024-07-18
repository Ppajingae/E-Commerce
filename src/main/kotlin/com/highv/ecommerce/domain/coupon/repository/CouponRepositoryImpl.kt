package com.highv.ecommerce.domain.coupon.repository

import com.highv.ecommerce.domain.coupon.entity.Coupon
import com.highv.ecommerce.domain.coupon.entity.QCoupon
import com.highv.ecommerce.domain.product.entity.QProduct
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CouponRepositoryImpl(
    private val couponJpaRepository: CouponJpaRepository,
    @PersistenceContext
    private val em : EntityManager
): CouponRepository {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(em)
    val coupon: QCoupon = QCoupon.coupon
    val product = QProduct.product

    override fun save(coupon: Coupon): Coupon {
        return couponJpaRepository.save(coupon)
    }

    override fun findByIdOrNull(id: Long): Coupon? {
        return couponJpaRepository.findByIdOrNull(id)
    }

    override fun delete(coupon: Coupon) {
        couponJpaRepository.delete(coupon)
    }

    override fun findAll(): List<Coupon> {
        return couponJpaRepository.findAll()
    }

    override fun findAllCouponIdWithBuyer(couponIdList: List<Long>): List<Coupon> {


       return queryFactory.selectFrom(coupon)
            .where(coupon.id.`in`(couponIdList))
            .leftJoin(coupon.product(),product)
            .fetchJoin()
            .fetch()
    }

    override fun existsByProductId(productId: Long): Boolean {
        return couponJpaRepository.existsByProductId(productId)
    }
}