package com.medical.management

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/diagnoses")
class DiagnosisController(private val repository: DiagnosisRepository) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    fun create(@RequestBody diagnosis: Diagnosis) = repository.save(diagnosis)
}
