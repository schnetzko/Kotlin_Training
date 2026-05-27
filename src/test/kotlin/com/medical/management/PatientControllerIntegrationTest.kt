package com.medical.management

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.profiles.active=test"])
class PatientControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `GET patients should return all patients`() {
        val response: ResponseEntity<Array<Patient>> = restTemplate.getForEntity("/patients", Array<Patient>::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Retrieved ${response.body?.size ?: 0} patients")
    }

    @Test
    fun `GET patients should return empty list when no patients exist`() {
        val response: ResponseEntity<Array<Patient>> = restTemplate.getForEntity("/patients", Array<Patient>::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `POST patients should create patient`() {
        val patient = Patient(
            first_name = "Integration",
            second_name = "Test",
            mailing_address = "Integration Test Address",
            zip_code = "99999",
            city = "Test City",
            country = "Test Country",
            phone = "999999999",
            date_of_birth = LocalDate.of(1990, 1, 1)
        )
        val response: ResponseEntity<Patient> = restTemplate.postForEntity("/patients", patient, Patient::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.first_name == "Integration")
        println("Created patient: ${response.body?.first_name}")
    }
}