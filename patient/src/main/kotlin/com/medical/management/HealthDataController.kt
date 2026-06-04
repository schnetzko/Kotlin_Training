package com.medical.management

import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(HealthDataController::class.java)

    @GetMapping
    fun getAll(): List<HealthData> {
        logger.info("GET /health-data - fetching all health data")
        val healthData = repository.findAll()
        logger.debug("GET /health-data - returning {} health data record(s)", healthData.size)
        return healthData
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody healthData: HealthData
    ): HealthData {
        logger.info("POST /health-data - creating health data record: {}", healthData)
        val saved = repository.save(healthData)
        logger.debug("POST /health-data - health data record created with id={}", saved.id)
        return saved
    }

    @GetMapping("/patients/{patientId}/prognosis")
    fun getPrognosis(
        @PathVariable patientId: Long
    ): SicknessPrognosis {
        logger.info("GET /health-data/patients/{}/prognosis - calculating prognosis for patientId={}", patientId, patientId)
        val prognosis = sicknessPrognosisService.calculatePrognosis(patientId)
        logger.debug("GET /health-data/patients/{}/prognosis - prognosis result: {}", patientId, prognosis)
        return prognosis
    }
}
