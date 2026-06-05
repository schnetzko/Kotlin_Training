package com.medical.treatment

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class TreatmentController(
    private val repository: TreatmentRepository
) {
    private val logger = LoggerFactory.getLogger(TreatmentController::class.java)
    
    @GetMapping("/")
    fun root(): String {
        logger.info("GET / - root endpoint called")
        return "Welcome to Medical Management API - Treatment"
    }
    
    @GetMapping("/treatments")
    fun getAll(): List<Treatment> {
        logger.info("GET /treatments - fetching all treatments")
        val treatments = repository.findAll()
        logger.debug("GET /treatments - returning {} treatment(s)", treatments.size)
        return treatments
    }

    @PostMapping("/treatments")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody treatment: Treatment
    ): Treatment {
        logger.info("POST /treatments - creating treatment: {}", treatment)
        val saved = repository.save(treatment)
        logger.debug("POST /treatments - treatment created with id={}", saved.id)
        return saved
    }
}
