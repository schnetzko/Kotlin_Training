package com.medical.examination

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

@RestController
class ExaminationController(
    private val repository: ExaminationRepository
) {
    private val logger = LoggerFactory.getLogger(ExaminationController::class.java)

    @GetMapping("/")
    fun root(): String {
        logger.info("GET / - root endpoint called")
        return "Welcome to Medical Management API - Examination"
    }

    @GetMapping("/examinations")
    fun getAll(): List<Examination> {
        logger.info("GET /examinations - fetching all examinations")
        val examinations = repository.findAll()
        logger.debug("GET /examinations - returning {} examination(s)", examinations.size)
        return examinations
    }

    @PostMapping("/examinations")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody examination: Examination
    ): Examination {
        logger.info("POST /examinations - creating examination: {}", examination)
        val saved = repository.save(examination)
        logger.debug("POST /examinations - examination created with id={}", saved.id)
        return saved
    }
}
