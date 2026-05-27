package com.medical.management

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class PatientIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var patientRepository: PatientRepository

    companion object {

        @Container
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")
            .withDatabaseName("medical_test")
            .withUsername("test")
            .withPassword("test")

        @JvmStatic
        @DynamicPropertySource
        fun overrideDataSourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            postgres.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            postgres.stop()
        }
    }

    @Test
    fun `POST patient creates patient and GET patients returns it`() {
        // Arrange – build the patient payload (no id, let the DB assign it)
        val newPatient = Patient(
            first_name = "Anna",
            second_name = "Müller",
            mailing_address = "Hauptstraße 1",
            zip_code = "10115",
            city = "Berlin",
            country = "Germany",
            phone = "+49 30 123456",
            date_of_birth = LocalDate.of(1985, 3, 22)
        )

        // Act – POST /patients
        val postResult = mockMvc.perform(
            post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPatient))
        )

        // Assert – 201 Created and the returned body matches the input
        postResult
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.first_name").value("Anna"))
            .andExpect(jsonPath("$.second_name").value("Müller"))
            .andExpect(jsonPath("$.city").value("Berlin"))
            .andExpect(jsonPath("$.country").value("Germany"))
            .andExpect(jsonPath("$.date_of_birth").value("1985-03-22"))

        // Act – GET /patients
        val getResult = mockMvc.perform(get("/patients"))

        // Assert – 200 OK and the previously created patient is present
        getResult
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.first_name == 'Anna')].second_name").value("Müller"))
            .andExpect(jsonPath("$[?(@.first_name == 'Anna')].country").value("Germany"))
    }
}
