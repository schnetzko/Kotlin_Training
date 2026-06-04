package com.medical.management

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/patients/{patientId}/prognosis")
class SicknessPrognosisController(
    private val sicknessPrognosisService: SicknessPrognosisService
) {
    private val logger = LoggerFactory.getLogger(SicknessPrognosisController::class.java)

    @GetMapping
    fun getPrognosis(@PathVariable patientId: Long): SicknessPrognosis {
        logger.info("GET /patients/{}/prognosis - calculating sickness prognosis", patientId)
        val prognosis = sicknessPrognosisService.calculatePrognosis(patientId)
        logger.debug("GET /patients/{}/prognosis - riskLevel={}", patientId, prognosis.riskLevel)
        return prognosis
    }
}
