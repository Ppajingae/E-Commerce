package com.highv.ecommerce.domain.backoffice.repository

import com.highv.ecommerce.domain.backoffice.entity.ProductBackOffice
import org.springframework.data.jpa.repository.JpaRepository

interface ProductBackOfficeRepository : JpaRepository<ProductBackOffice, Long> {
    fun findAllByShopId(shopId: Long): List<ProductBackOffice>
}