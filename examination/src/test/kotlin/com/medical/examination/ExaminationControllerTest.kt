package com.medical.examination

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

@WebMvcTest(ExaminationController::class)
class ExaminationControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var repository: ExaminationRepository

    private val patient =
        Patient(
            id = 1L,
            first_name = "John",
            second_name = "Doe",
            country = "Germany"
        )

    private val examination1 =
        Examination(
            id = 1L,
            specialist = "Dr. Smith",
            name = "Annual Checkup",
            date = "2024-01-15",
            type = "ROUTINE",
            patient = patient
        )

    private val examination2 =
        Examination(
            id = 2L,
            specialist = "Dr. Jones",
            name = "Blood Test",
            date = "2024-02-20",
            type = "LABORATORY",
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
            .andExpect(content().string("Welcome to Medical Management API - Examination")) 
    }

    @Test
    fun `GET examinations should return all examinations`() {
        `when`(repository.findAll()).thenReturn(listOf(examination1, examination2))

        mockMvc
            .perform(get("/examinations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].name").value("Annual Checkup"))
            .andExpect(jsonPath("$.[1].name").value("Blood Test"))

        verify(repository).findAll()
    }

    @Test
    fun `GET examinations should return empty list when no examinations`() {
        `when`(repository.findAll()).thenReturn(emptyList())

        mockMvc
            .perform(get("/examinations"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(emptyList<Any>())))

        verify(repository).findAll()
    }

    @Test
    fun `POST examinations should create examination`() {
        val newExamination =
            Examination(
                specialist = "Dr. New",
                name = "New Examination",
                date = "2024-03-01",
                type = "NEW_TYPE",
                patient = patient
            )

        `when`(repository.save(any(Examination::class.java))).thenAnswer { invocation ->
            val saved = invocation.arguments[0] as Examination
            saved.copy(id = 100L)
        }

        mockMvc
            .perform(
                post("/examinations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newExamination))
            ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Examination"))
            .andExpect(jsonPath("$.specialist").value("Dr. New"))

        verify(repository).save(any(Examination::class.java))
    }
}
