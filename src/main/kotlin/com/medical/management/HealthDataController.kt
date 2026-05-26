package com.medical.management

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/health-data")
class HealthDataController(private val repository: HealthDataRepository) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    fun create(@RequestBody healthData: HealthData) = repository.save(healthData)
}
