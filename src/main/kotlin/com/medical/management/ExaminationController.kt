package com.medical.management

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/examinations")
class ExaminationController(private val repository: ExaminationRepository) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody examination: Examination) = repository.save(examination)
}
