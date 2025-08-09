package com.zzan.zzan.common.exception
import org.springframework.http.HttpStatus

class CustomException(
    val status: HttpStatus,
    override val message: String? = null
) : RuntimeException(message ?: status.reasonPhrase)
