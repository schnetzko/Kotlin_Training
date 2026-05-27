package com.medical.management

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

class SicknessPrognosisServiceTest {

    private val healthDataRepository: HealthDataRepository = mock(HealthDataRepository::class.java)
    private val service = SicknessPrognosisService(healthDataRepository)

    private val patient = Patient(
        id = 1L,
        first_name = "John",
        second_name = "Doe",
        country = "Germany"
    )

    private fun healthData(
        weight: Double = 70.0,
        size: Double = 175.0,
        bloodPressure: String = "120/80",
        bloodCount: String = "Normal",
        urineSample: String = "Normal",
        allergies: String = "None"
    ) = HealthData(
        id = 1L,
        patient = patient,
        date = LocalDate.now(),
        size = size,
        weight = weight,
        blood_count = bloodCount,
        blood_pressure = bloodPressure,
        urine_sample = urineSample,
        bloodType = "A+",
        allergies = allergies
    )

    @Test
    fun `calculatePrognosis throws 422 when no health data exists`() {
        `when`(healthDataRepository.findByPatientId(1L)).thenReturn(emptyList())

        val ex = assertThrows<ResponseStatusException> {
            service.calculatePrognosis(1L)
        }
        assertEquals(422, ex.statusCode.value())
    }

    @Test
    fun `calculatePrognosis returns LOW risk for healthy patient`() {
        `when`(healthDataRepository.findByPatientId(1L)).thenReturn(
            listOf(healthData(weight = 70.0, size = 175.0, bloodPressure = "120/80"))
        )

        val prognosis = service.calculatePrognosis(1L)

        assertEquals(RiskLevel.LOW, prognosis.riskLevel)
        assertEquals(1L, prognosis.patientId)
        assertTrue(prognosis.potentialConditions.isNotEmpty())
        assertTrue(prognosis.recommendations.isNotEmpty())
    }

    @Test
    fun `calculatePrognosis returns HIGH risk for obese patient with hypertension and abnormal blood count`() {
        `when`(healthDataRepository.findByPatientId(1L)).thenReturn(
            listOf(
                healthData(
                    weight = 120.0,
                    size = 175.0,
                    bloodPressure = "150/95",
                    bloodCount = "abnormal"
                )
            )
        )

        val prognosis = service.calculatePrognosis(1L)

        assertEquals(RiskLevel.HIGH, prognosis.riskLevel)
        assertTrue(prognosis.potentialConditions.any { it.contains("Obesity") })
        assertTrue(prognosis.potentialConditions.any { it.contains("Hypertension") })
        assertTrue(prognosis.potentialConditions.any { it.contains("Anemia") })
    }

    @Test
    fun `calculatePrognosis returns MODERATE risk for overweight patient with stage 1 hypertension`() {
        `when`(healthDataRepository.findByPatientId(1L)).thenReturn(
            listOf(healthData(weight = 90.0, size = 175.0, bloodPressure = "135/85"))
        )

        val prognosis = service.calculatePrognosis(1L)

        assertEquals(RiskLevel.MODERATE, prognosis.riskLevel)
        assertTrue(prognosis.potentialConditions.any { it.contains("Metabolic syndrome") })
        assertTrue(prognosis.potentialConditions.any { it.contains("Hypertension (Stage 1)") })
    }

    @Test
    fun `calculatePrognosis detects kidney disease risk from abnormal urine sample`() {
        `when`(healthDataRepository.findByPatientId(1L)).thenReturn(
            listOf(healthData(urineSample = "protein detected"))
        )

        val prognosis = service.calculatePrognosis(1L)

        assertTrue(prognosis.potentialConditions.any { it.contains("Kidney disease") })
        assertTrue(prognosis.recommendations.any { it.contains("nephrologist") })
    }

    @Test
    fun `calculatePrognosis detects allergy risk when allergies are present`() {
        `when`(healthDataRepository.findByPatientId(1L)).thenReturn(
            listOf(healthData(allergies = "Penicillin"))
        )

        val prognosis = service.calculatePrognosis(1L)

        assertTrue(prognosis.potentialConditions.any { it.contains("Allergic reactions") })
        assertTrue(prognosis.recommendations.any { it.contains("allergy kit") })
    }

    @Test
    fun `calculatePrognosis uses most recent health data record`() {
        val older = healthData(weight = 120.0, size = 175.0).copy(
            id = 1L,
            date = LocalDate.now().minusDays(10)
        )
        val newer = healthData(weight = 70.0, size = 175.0).copy(
            id = 2L,
            date = LocalDate.now()
        )
        `when`(healthDataRepository.findByPatientId(1L)).thenReturn(listOf(older, newer))

        val prognosis = service.calculatePrognosis(1L)

        // Newer record has healthy BMI → no obesity condition
        assertTrue(prognosis.potentialConditions.none { it.contains("Obesity") })
    }

    @Test
    fun `calculatePrognosis detects hypotension`() {
        `when`(healthDataRepository.findByPatientId(1L)).thenReturn(
            listOf(healthData(bloodPressure = "85/55"))
        )

        val prognosis = service.calculatePrognosis(1L)

        assertTrue(prognosis.potentialConditions.any { it.contains("Hypotension") })
        assertTrue(prognosis.recommendations.any { it.contains("fluid intake") })
    }

    @Test
    fun `calculatePrognosis detects underweight risk`() {
        `when`(healthDataRepository.findByPatientId(1L)).thenReturn(
            listOf(healthData(weight = 45.0, size = 175.0))
        )

        val prognosis = service.calculatePrognosis(1L)

        assertTrue(prognosis.potentialConditions.any { it.contains("Malnutrition") })
    }
}
