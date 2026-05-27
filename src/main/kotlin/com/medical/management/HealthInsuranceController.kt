package com.medical.management

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/health-insurances")
class HealthInsuranceController(private val repository: HealthInsuranceRepository) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody healthInsurance: HealthInsurance) = repository.save(healthInsurance)
}
