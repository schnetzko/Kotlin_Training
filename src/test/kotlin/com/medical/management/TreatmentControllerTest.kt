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

@WebMvcTest(TreatmentController::class)
class TreatmentControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var repository: TreatmentRepository

    private val patient =
        Patient(
            id = 1L,
            first_name = "John",
            second_name = "Doe",
            country = "Germany"
        )

    private val treatment1 =
        Treatment(
            id = 1L,
            specialist = "Dr. Smith",
            name = "Physical Therapy",
            date = "2024-01-15",
            type = "PHYSIOTHERAPY",
            patient = patient
        )

    private val treatment2 =
        Treatment(
            id = 2L,
            specialist = "Dr. Jones",
            name = "Medication",
            date = "2024-02-20",
            type = "MEDICATION",
            patient = patient
        )

    @BeforeEach
    fun setup() {
        reset(repository)
    }

    @Test
    fun `GET treatments should return all treatments`() {
        `when`(repository.findAll()).thenReturn(listOf(treatment1, treatment2))

        mockMvc
            .perform(get("/treatments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].name").value("Physical Therapy"))
            .andExpect(jsonPath("$.[1].name").value("Medication"))

        verify(repository).findAll()
    }

    @Test
    fun `GET treatments should return empty list when no treatments`() {
        `when`(repository.findAll()).thenReturn(emptyList())

        mockMvc
            .perform(get("/treatments"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(emptyList<Any>())))

        verify(repository).findAll()
    }

    @Test
    fun `POST treatments should create treatment`() {
        val newTreatment =
            Treatment(
                specialist = "Dr. New",
                name = "New Treatment",
                date = "2024-03-01",
                type = "NEW_TYPE",
                patient = patient
            )

        `when`(repository.save(any(Treatment::class.java))).thenAnswer { invocation ->
            val saved = invocation.arguments[0] as Treatment
            saved.copy(id = 100L)
        }

        mockMvc
            .perform(
                post("/treatments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newTreatment))
            ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Treatment"))
            .andExpect(jsonPath("$.specialist").value("Dr. New"))

        verify(repository).save(any(Treatment::class.java))
    }
}
