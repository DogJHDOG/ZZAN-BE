package com.zzan.zzan.common.exception

import com.zzan.zzan.common.response.ApiResponse
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler : KLogging() {
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Custom exception occurred: ${e.message} ", e)

        val errorResponse = ApiResponse.error<Nothing>(e.message ?: "오류가 발생했습니다");
        return ResponseEntity.status(e.status)
            .body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnhandledException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("Unhandled exception occurred: ${e.message}", e)

        val errorResponse = ApiResponse.error<Nothing>("서버 내부 오류가 발생했습니다")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse)
    }
}