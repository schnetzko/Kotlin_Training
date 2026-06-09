package com.medical.diagnosis

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.HttpStatus

@RestController
class DiagnosisController (
    private val repository: DiagnosisRepository
) {
    private val logger = LoggerFactory.getLogger(DiagnosisController::class.java)

    @GetMapping("/")
    fun root(): String {
        logger.info("GET / - root endpoint called")
        return "Welcome to Medical Management API - Diagnosis"
    }

    @GetMapping("/diagnoses")
    fun getAll(): List<Diagnosis> {
        logger.info("GET /diagnoses - fetching all diagnoses")
        val diagnoses = repository.findAll()
        logger.debug("GET /diagnoses - returning {} diagnosis/diagnoses", diagnoses.size)
        return diagnoses
    }

    @PostMapping("/diagnoses")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody diagnosis: Diagnosis
    ): Diagnosis {
        logger.info("POST /diagnoses - creating diagnosis: {}", diagnosis)
        val saved = repository.save(diagnosis)
        logger.debug("POST /diagnoses - diagnosis created with id={}", saved.id)
        return saved
    }
}
