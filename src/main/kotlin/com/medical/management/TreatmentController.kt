package com.medical.management

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/treatments")
class TreatmentController(private val repository: TreatmentRepository) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody treatment: Treatment) = repository.save(treatment)
}
