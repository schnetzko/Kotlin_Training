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

@WebMvcTest(DrugTherapyController::class)
class DrugTherapyControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var repository: DrugTherapyRepository

    private val drugTherapy1 =
        DrugTherapy(
            id = 1L,
            drugName = "Paracetamol",
            description = "Pain reliever",
            activeIngredient = "Acetaminophen",
            dosage = "500mg",
            frequence = "3 times a day"
        )

    private val drugTherapy2 =
        DrugTherapy(
            id = 2L,
            drugName = "Ibuprofen",
            description = "Anti-inflammatory",
            activeIngredient = "Ibuprofen",
            dosage = "400mg",
            frequence = "2 times a day"
        )

    @BeforeEach
    fun setup() {
        reset(repository)
    }

    @Test
    fun `GET drug-therapies should return all drug therapies`() {
        `when`(repository.findAll()).thenReturn(listOf(drugTherapy1, drugTherapy2))

        mockMvc
            .perform(get("/drug-therapies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].drugName").value("Paracetamol"))
            .andExpect(jsonPath("$.[1].drugName").value("Ibuprofen"))

        verify(repository).findAll()
    }

    @Test
    fun `GET drug-therapies should return empty list when no drug therapies`() {
        `when`(repository.findAll()).thenReturn(emptyList())

        mockMvc
            .perform(get("/drug-therapies"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(emptyList<Any>())))

        verify(repository).findAll()
    }

    @Test
    fun `POST drug-therapies should create drug therapy`() {
        val newDrugTherapy =
            DrugTherapy(
                drugName = "New Drug",
                description = "New description",
                activeIngredient = "New Ingredient",
                dosage = "200mg",
                frequence = "Once daily"
            )

        `when`(repository.save(any(DrugTherapy::class.java))).thenAnswer { invocation ->
            val saved = invocation.arguments[0] as DrugTherapy
            saved.copy(id = 100L)
        }

        mockMvc
            .perform(
                post("/drug-therapies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newDrugTherapy))
            ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.drugName").value("New Drug"))
            .andExpect(jsonPath("$.dosage").value("200mg"))

        verify(repository).save(any(DrugTherapy::class.java))
    }
}
