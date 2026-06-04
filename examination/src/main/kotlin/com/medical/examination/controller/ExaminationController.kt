package com.medical.examination.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/examinations")
class ExaminationController {

    @GetMapping
    fun list() = mapOf("service" to "examination", "status" to "ok")
}
