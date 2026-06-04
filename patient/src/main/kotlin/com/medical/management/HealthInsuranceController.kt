package com.medical.management

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health-insurances")
class HealthInsuranceController(
    private val repository: HealthInsuranceRepository
) {
    private val logger = LoggerFactory.getLogger(HealthInsuranceController::class.java)

    @GetMapping
    fun getAll(): List<HealthInsurance> {
        logger.info("GET /health-insurances - fetching all health insurances")
        val insurances = repository.findAll()
        logger.debug("GET /health-insurances - returning {} health insurance(s)", insurances.size)
        return insurances
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody healthInsurance: HealthInsurance
    ): HealthInsurance {
        logger.info("POST /health-insurances - creating health insurance: {}", healthInsurance)
        val saved = repository.save(healthInsurance)
        logger.debug("POST /health-insurances - health insurance created with id={}", saved.id)
        return saved
    }
}
