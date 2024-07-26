package com.highv.ecommerce.domain.backoffice.admin.dto

data class BlackListResponse(
    val nickname: String,
    val email: String,
    val sanctionsCount: Int,
    val isSanctioned: Boolean
)