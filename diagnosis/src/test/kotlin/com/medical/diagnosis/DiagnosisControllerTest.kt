package com.medical.diagnosis

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

@WebMvcTest(DiagnosisController::class)
class DiagnosisControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var repository: DiagnosisRepository

    private val patient =
        Patient(
            id = 1L,
            first_name = "John",
            second_name = "Doe",
            country = "Germany"
        )

    private val diagnosis1 =
        Diagnosis(
            id = 1L,
            name = "Common Cold",
            code = "A00.0",
            specialist = "Dr. Smith",
            date = LocalDate.now(),
            type = DiagnosisType.COMMON_AILMENTS,
            patient = patient
        )

    private val diagnosis2 =
        Diagnosis(
            id = 2L,
            name = "Flu",
            code = "J10",
            specialist = "Dr. Jones",
            date = LocalDate.now().plusDays(1),
            type = DiagnosisType.INFECTIOUS,
            patient = patient
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
            .andExpect(content().string("Welcome to Medical Management API - Diagnosis"))
    }

    @Test
    fun `GET diagnoses should return all diagnoses`() {
        `when`(repository.findAll()).thenReturn(listOf(diagnosis1, diagnosis2))

        mockMvc
            .perform(get("/diagnoses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].name").value("Common Cold"))
            .andExpect(jsonPath("$.[1].name").value("Flu"))

        verify(repository).findAll()
    }

    @Test
    fun `GET diagnoses should return empty list when no diagnoses`() {
        `when`(repository.findAll()).thenReturn(emptyList())

        mockMvc
            .perform(get("/diagnoses"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(emptyList<Any>())))

        verify(repository).findAll()
    }

    @Test
    fun `POST diagnoses should create diagnosis`() {
        val newDiagnosis =
            Diagnosis(
                name = "New Diagnosis",
                code = "Z00.0",
                specialist = "Dr. New",
                date = LocalDate.now(),
                type = DiagnosisType.CHRONIC_DISEASES,
                patient = patient
            )

        `when`(repository.save(any(Diagnosis::class.java))).thenAnswer { invocation ->
            val saved = invocation.arguments[0] as Diagnosis
            saved.copy(id = 100L)
        }

        mockMvc
            .perform(
                post("/diagnoses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newDiagnosis))
            ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Diagnosis"))
            .andExpect(jsonPath("$.type").value("CHRONIC_DISEASES"))

        verify(repository).save(any(Diagnosis::class.java))
    }
}
