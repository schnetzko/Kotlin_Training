package com.medical.management

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/drug-therapies")
class DrugTherapyController(private val repository: DrugTherapyRepository) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody drugTherapy: DrugTherapy) = repository.save(drugTherapy)
}
