package com.highv.ecommerce.domain.order_master.repository

import com.highv.ecommerce.domain.order_master.entity.OrderMaster

interface OrderMasterRepository {

    fun saveAndFlush(productsOrder: OrderMaster): OrderMaster

    fun findByIdOrNull(Id: Long): OrderMaster?

    fun save(productsOrder: OrderMaster): OrderMaster

    fun discountTotalPriceList(buyerId: Long, couponIdList: List<Long>): Int

    fun findByIdIn(ids: List<Long>): List<OrderMaster>
}