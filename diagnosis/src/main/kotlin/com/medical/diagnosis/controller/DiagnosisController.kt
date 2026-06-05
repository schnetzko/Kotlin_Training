package com.medical.diagnosis.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DiagnosisController {
    private val logger = LoggerFactory.getLogger(DiagnosisController::class.java)

    @GetMapping("/")
    fun root(): String {
        logger.info("GET / - root endpoint called")
        return "Welcome to Medical Management API - Diagnosis"
    }

    @GetMapping("/diagnoses")
    fun list() = mapOf("service" to "diagnosis", "status" to "ok")
}
