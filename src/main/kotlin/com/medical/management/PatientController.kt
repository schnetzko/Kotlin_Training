package com.medical.management

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/patients")
class PatientController(private val repository: PatientRepository) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    fun create(@RequestBody patient: Patient) = repository.save(patient)
}
