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

@WebMvcTest(HealthInsuranceController::class)
class HealthInsuranceControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var repository: HealthInsuranceRepository

    private val contact =
        Contact(
            id = 1L,
            firstName = "Insurance",
            secondName = "Company",
            mailingAddress = "Insure St",
            zipCode = "12345",
            city = "Berlin",
            country = "Germany",
            phone = "123456789",
            email = "info@insurance.com",
            type = "CONTACT"
        )

    private val patient =
        Patient(
            id = 1L,
            first_name = "John",
            second_name = "Doe",
            country = "Germany"
        )

    private val healthInsurance1 =
        HealthInsurance(
            id = 1L,
            name = "Health Plus",
            insuranceNumber = "HP123456",
            contact = contact,
            coverage = Coverage.BY_LAW,
            patient = patient
        )

    private val healthInsurance2 =
        HealthInsurance(
            id = 2L,
            name = "Basic Care",
            insuranceNumber = "BC789012",
            contact = contact,
            coverage = Coverage.PRIVATE,
            patient = patient
        )

    @BeforeEach
    fun setup() {
        reset(repository)
    }

    @Test
    fun `GET health-insurances should return all health insurances`() {
        `when`(repository.findAll()).thenReturn(listOf(healthInsurance1, healthInsurance2))

        mockMvc
            .perform(get("/health-insurances"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].name").value("Health Plus"))
            .andExpect(jsonPath("$.[1].name").value("Basic Care"))

        verify(repository).findAll()
    }

    @Test
    fun `GET health-insurances should return empty list when no health insurances`() {
        `when`(repository.findAll()).thenReturn(emptyList())

        mockMvc
            .perform(get("/health-insurances"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(emptyList<Any>())))

        verify(repository).findAll()
    }

    @Test
    fun `POST health-insurances should create health insurance`() {
        val newHealthInsurance =
            HealthInsurance(
                name = "New Insurance",
                insuranceNumber = "NI999999",
                contact = contact,
                coverage = Coverage.BY_LAW
            )

        `when`(repository.save(any(HealthInsurance::class.java))).thenAnswer { invocation ->
            val saved = invocation.arguments[0] as HealthInsurance
            saved.copy(id = 100L)
        }

        mockMvc
            .perform(
                post("/health-insurances")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newHealthInsurance))
            ).andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Insurance"))
            .andExpect(jsonPath("$.insuranceNumber").value("NI999999"))

        verify(repository).save(any(HealthInsurance::class.java))
    }
}
