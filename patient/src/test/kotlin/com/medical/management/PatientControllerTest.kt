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

@WebMvcTest(PatientController::class)
class PatientControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var repository: PatientRepository

    private val patient1 =
        Patient(
            id = 1L,
            first_name = "John",
            second_name = "Doe",
            country = "Germany",
            date_of_birth = LocalDate.of(1990, 5, 15),
            job_category = "Engineer"
        )

    private val patient2 =
        Patient(
            id = 2L,
            first_name = "Jane",
            second_name = "Smith",
            country = "Austria",
            date_of_birth = LocalDate.of(1985, 10, 20),
            job_category = "Teacher"
        )

    @BeforeEach
    fun setup() {
        reset(repository)
    }

    @Test
    fun `GET root should return welcome message`() {
        mockMvc
            .perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string("Welcome to Medical Management API - Patient"))
    }

    @Test
    fun `GET patients should return all patients`() {
        `when`(repository.findAll()).thenReturn(listOf(patient1, patient2))

        mockMvc
            .perform(get("/patients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].first_name").value("John"))
            .andExpect(jsonPath("$.[1].first_name").value("Jane"))

        verify(repository).findAll()
    }

    @Test
    fun `GET patients should return empty list when no patients`() {
        `when`(repository.findAll()).thenReturn(emptyList())

        mockMvc
            .perform(get("/patients"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(emptyList<Any>())))

        verify(repository).findAll()
    }

    @Test
    fun `POST patients should create patient`() {
        val newPatient =
            Patient(
                first_name = "New",
                second_name = "Patient",
                country = "France",
                date_of_birth = LocalDate.of(2000, 1, 1)
            )

        `when`(repository.save(any(Patient::class.java))).thenAnswer { invocation ->
            val saved = invocation.arguments[0] as Patient
            saved.copy(id = 100L)
        }

        mockMvc
            .perform(
                post("/patients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newPatient))
            ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.first_name").value("New"))
            .andExpect(jsonPath("$.country").value("France"))

        verify(repository).save(any(Patient::class.java))
    }
}
