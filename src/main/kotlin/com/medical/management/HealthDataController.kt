package com.medical.management

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health-data")
class HealthDataController(
    private val repository: HealthDataRepository,
    private val sicknessPrognosisService: SicknessPrognosisService
) {
    @GetMapping
    fun getAll() = repository.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody healthData: HealthData
    ) = repository.save(healthData)

    @GetMapping("/patients/{patientId}/prognosis")
    fun getPrognosis(
        @PathVariable patientId: Long
    ) = sicknessPrognosisService.calculatePrognosis(patientId)
}
