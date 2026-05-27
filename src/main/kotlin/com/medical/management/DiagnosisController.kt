package com.medical.management

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/diagnoses")
class DiagnosisController(
    private val repository: DiagnosisRepository
) {
    private val logger = LoggerFactory.getLogger(DiagnosisController::class.java)

    @GetMapping
    fun getAll(): List<Diagnosis> {
        logger.info("GET /diagnoses - fetching all diagnoses")
        val diagnoses = repository.findAll()
        logger.debug("GET /diagnoses - returning {} diagnosis/diagnoses", diagnoses.size)
        return diagnoses
    }

    @PostMapping
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
