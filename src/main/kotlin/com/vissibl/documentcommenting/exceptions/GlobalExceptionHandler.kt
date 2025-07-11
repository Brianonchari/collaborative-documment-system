package com.vissibl.documentcommenting.exceptions

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    private val DEFAULT_ERROR_MESSAGE = "An unexpected error occurred"

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun onResourceNotFoundException(
        e: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail> {
        val errorMessage = e.localizedMessage ?: DEFAULT_ERROR_MESSAGE
        val problemDetail = createProblemDetail(HttpStatus.NOT_FOUND, errorMessage)
        return ResponseEntity.status(problemDetail.status).body(problemDetail)
    }

    private fun createProblemDetail(status: HttpStatusCode, message: String): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, message)
        return problemDetail
    }

}