package com.medical.treatment.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/treatments")
class TreatmentController {

    @GetMapping
    fun list() = mapOf("service" to "treatment", "status" to "ok")
}
