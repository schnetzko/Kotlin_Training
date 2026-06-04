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
class PatientController(
    private val repository: PatientRepository
) {
    private val logger = LoggerFactory.getLogger(PatientController::class.java)

    
    @GetMapping("/")
    fun root(): String {
        logger.info("GET / - root endpoint called")
        return "Welcome to Medical Management API - Patient"
    }
    
    @GetMapping("/patients")
    fun getAll(): List<Patient> {
        logger.info("GET /patients - fetching all patients")
        val patients = repository.findAll()
        logger.debug("GET /patients - returning {} patient(s)", patients.size)
        return patients
    }

    @PostMapping("/patients")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody patient: Patient
    ): Patient {
        logger.info("POST /patients - creating patient: {}", patient)
        val saved = repository.save(patient)
        logger.debug("POST /patients - patient created with id={}", saved.id)
        return saved
    }
}
