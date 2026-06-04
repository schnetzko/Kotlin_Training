package com.medical.management

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@WebMvcTest(HealthDataController::class)
class HealthDataControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var repository: HealthDataRepository

    @MockBean
    lateinit var sicknessPrognosisService: SicknessPrognosisService

    private val patient =
        Patient(
            id = 1L,
            first_name = "John",
            second_name = "Doe",
            country = "Germany"
        )

    private val healthData1 =
        HealthData(
            id = 1L,
            patient = patient,
            date = LocalDate.now(),
            size = 175.5,
            weight = 70.2,
            blood_count = "Normal",
            blood_pressure = "120/80",
            urine_sample = "Normal",
            bloodType = "A+",
            allergies = "None"
        )

    private val healthData2 =
        HealthData(
            id = 2L,
            patient = patient,
            date = LocalDate.now().plusDays(1),
            size = 175.5,
            weight = 70.5,
            blood_count = "Normal",
            blood_pressure = "115/75",
            urine_sample = "Normal",
            bloodType = "A+",
            allergies = "Penicillin"
        )

    @BeforeEach
    fun setup() {
        reset(repository)
        reset(sicknessPrognosisService)
    }

    @Test
    fun `GET health-data should return all health data`() {
        `when`(repository.findAll()).thenReturn(listOf(healthData1, healthData2))

        mockMvc
            .perform(get("/health-data"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].bloodType").value("A+"))
            .andExpect(jsonPath("$.[1].bloodType").value("A+"))

        verify(repository).findAll()
    }

    @Test
    fun `GET health-data should return empty list when no health data`() {
        `when`(repository.findAll()).thenReturn(emptyList())

        mockMvc
            .perform(get("/health-data"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(emptyList<Any>())))

        verify(repository).findAll()
    }

    @Test
    fun `POST health-data should create health data`() {
        val newHealthData =
            HealthData(
                patient = patient,
                date = LocalDate.now(),
                size = 180.0,
                weight = 80.0,
                blood_count = "Normal",
                blood_pressure = "130/85",
                urine_sample = "Normal",
                bloodType = "B+",
                allergies = "None"
            )

        `when`(repository.save(any(HealthData::class.java))).thenAnswer { invocation ->
            val saved = invocation.arguments[0] as HealthData
            saved.copy(id = 100L)
        }

        mockMvc
            .perform(
                post("/health-data")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newHealthData))
            ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.bloodType").value("B+"))
            .andExpect(jsonPath("$.weight").value(80.0))

        verify(repository).save(any(HealthData::class.java))
    }

    @Test
    fun `GET health-data patients prognosis should return prognosis for patient`() {
        val prognosis = SicknessPrognosis(
            patientId = 1L,
            riskLevel = RiskLevel.LOW,
            potentialConditions = listOf("No significant risk factors detected"),
            recommendations = listOf("Maintain current healthy lifestyle and schedule annual check-ups")
        )
        `when`(sicknessPrognosisService.calculatePrognosis(1L)).thenReturn(prognosis)

        mockMvc
            .perform(get("/health-data/patients/1/prognosis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.patientId").value(1))
            .andExpect(jsonPath("$.riskLevel").value("LOW"))
            .andExpect(jsonPath("$.potentialConditions[0]").value("No significant risk factors detected"))
            .andExpect(jsonPath("$.recommendations[0]").value("Maintain current healthy lifestyle and schedule annual check-ups"))

        verify(sicknessPrognosisService).calculatePrognosis(1L)
    }

    @Test
    fun `GET health-data patients prognosis should return HIGH risk prognosis`() {
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
            .perform(get("/health-data/patients/2/prognosis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.patientId").value(2))
            .andExpect(jsonPath("$.riskLevel").value("HIGH"))
            .andExpect(jsonPath("$.potentialConditions.length()").value(3))
            .andExpect(jsonPath("$.recommendations.length()").value(3))

        verify(sicknessPrognosisService).calculatePrognosis(2L)
    }

    @Test
    fun `GET health-data patients prognosis should return 422 when no health data available`() {
        `when`(sicknessPrognosisService.calculatePrognosis(99L))
            .thenThrow(ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No complete health data available for patient 99"))

        mockMvc
            .perform(get("/health-data/patients/99/prognosis"))
            .andExpect(status().isUnprocessableEntity())

        verify(sicknessPrognosisService).calculatePrognosis(99L)
    }
}
