package com.medical.management

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/drug-therapies")
class DrugTherapyController(private val repository: DrugTherapyRepository) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    fun create(@RequestBody drugTherapy: DrugTherapy) = repository.save(drugTherapy)
}
