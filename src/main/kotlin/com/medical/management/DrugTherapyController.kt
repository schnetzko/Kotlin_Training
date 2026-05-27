package com.medical.management

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/drug-therapies")
class DrugTherapyController(
    private val repository: DrugTherapyRepository
) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody drugTherapy: DrugTherapy
    ) = repository.save(drugTherapy)
}
