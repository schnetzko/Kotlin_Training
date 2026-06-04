package com.medical.management

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.server.ResponseStatusException

@WebMvcTest(SicknessPrognosisController::class)
class SicknessPrognosisControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var sicknessPrognosisService: SicknessPrognosisService

    @BeforeEach
    fun setup() {
        reset(sicknessPrognosisService)
    }

    @Test
    fun `GET prognosis should return prognosis for patient`() {
        val prognosis = SicknessPrognosis(
            patientId = 1L,
            riskLevel = RiskLevel.LOW,
            potentialConditions = listOf("No significant risk factors detected"),
            recommendations = listOf("Maintain current healthy lifestyle and schedule annual check-ups")
        )
        `when`(sicknessPrognosisService.calculatePrognosis(1L)).thenReturn(prognosis)

        mockMvc
            .perform(get("/patients/1/prognosis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.patientId").value(1))
            .andExpect(jsonPath("$.riskLevel").value("LOW"))
            .andExpect(jsonPath("$.potentialConditions[0]").value("No significant risk factors detected"))
            .andExpect(jsonPath("$.recommendations[0]").value("Maintain current healthy lifestyle and schedule annual check-ups"))

        verify(sicknessPrognosisService).calculatePrognosis(1L)
    }

    @Test
    fun `GET prognosis should return HIGH risk prognosis`() {
        val prognosis = SicknessPrognosis(
            patientId = 2L,
            riskLevel = RiskLevel.HIGH,
            potentialConditions = listOf(
                "Obesity-related conditions (Type 2 Diabetes, Hypertension)",
                "Hypertension (Stage 2)",
                "Anemia or blood disorder"
            ),
            recommendations = listOf(
                "Consult a nutritionist and adopt a calorie-controlled diet",
                "Seek immediate medical evaluation for high blood pressure",
                "Schedule a complete blood panel with a hematologist"
            )
        )
        `when`(sicknessPrognosisService.calculatePrognosis(2L)).thenReturn(prognosis)

        mockMvc
            .perform(get("/patients/2/prognosis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.patientId").value(2))
            .andExpect(jsonPath("$.riskLevel").value("HIGH"))
            .andExpect(jsonPath("$.potentialConditions.length()").value(3))
            .andExpect(jsonPath("$.recommendations.length()").value(3))

        verify(sicknessPrognosisService).calculatePrognosis(2L)
    }

    @Test
    fun `GET prognosis should return 422 when no health data available`() {
        `when`(sicknessPrognosisService.calculatePrognosis(99L))
            .thenThrow(ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No complete health data available for patient 99"))

        mockMvc
            .perform(get("/patients/99/prognosis"))
            .andExpect(status().isUnprocessableEntity())

        verify(sicknessPrognosisService).calculatePrognosis(99L)
    }
}
