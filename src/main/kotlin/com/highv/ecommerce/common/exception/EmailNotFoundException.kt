package com.highv.ecommerce.common.exception

data class EmailNotFoundException(
    override val errorCode: Int = 404,
    override val message: String
) : CustomRuntimeException(errorCode, message)

