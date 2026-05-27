package com.medical.management

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@WebMvcTest(HealthDataController::class)
class HealthDataControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var repository: HealthDataRepository

    private val patient = Patient(
        id = 1L,
        first_name = "John",
        second_name = "Doe",
        country = "Germany"
    )

    private val healthData1 = HealthData(
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

    private val healthData2 = HealthData(
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
    }

    @Test
    fun `GET health-data should return all health data`() {
        `when`(repository.findAll()).thenReturn(listOf(healthData1, healthData2))

        mockMvc.perform(get("/health-data"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].bloodType").value("A+"))
            .andExpect(jsonPath("$.[1].bloodType").value("A+"))

        verify(repository).findAll()
    }

    @Test
    fun `GET health-data should return empty list when no health data`() {
        `when`(repository.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/health-data"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(emptyList<Any>())))

        verify(repository).findAll()
    }

    @Test
    fun `POST health-data should create health data`() {
        val newHealthData = HealthData(
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

        mockMvc.perform(post("/health-data")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newHealthData)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.bloodType").value("B+"))
            .andExpect(jsonPath("$.weight").value(80.0))

        verify(repository).save(any(HealthData::class.java))
    }
}