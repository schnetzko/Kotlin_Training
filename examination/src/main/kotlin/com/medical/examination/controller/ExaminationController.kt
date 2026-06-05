package com.medical.examination.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ExaminationController {
    private val logger = LoggerFactory.getLogger(ExaminationController::class.java)

    @GetMapping("/")
    fun root(): String {
        logger.info("GET / - root endpoint called")
        return "Welcome to Medical Management API - Examination"
    }

    @GetMapping("/examinations")
    fun list() = mapOf("service" to "examination", "status" to "ok")
}
