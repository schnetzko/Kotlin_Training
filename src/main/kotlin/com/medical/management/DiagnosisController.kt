package com.medical.management

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
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody diagnosis: Diagnosis
    ) = repository.save(diagnosis)
}
