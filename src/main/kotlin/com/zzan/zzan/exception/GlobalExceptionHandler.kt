package com.zzan.zzan.exception

import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : KLogging() {
    @ExceptionHandler(Exception::class)
    fun exceptionHandler(e: Exception): ResponseEntity<*> {
        logger.error("Unhandled exception occurred: ${e.message}", e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf(
                "success" to false,
                "error" to "INTERNAL_SERVER_ERROR",
                "message" to "서버 내부 오류가 발생했습니다",
                "timestamp" to System.currentTimeMillis()
            ))
    }
}