package com.medical.diagnosis.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/diagnoses")
class DiagnosisController {

    @GetMapping
    fun list() = mapOf("service" to "diagnosis", "status" to "ok")
}
