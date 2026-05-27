package com.medical.management

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.profiles.active=test"])
class HealthDataControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `GET health-data should return all health data`() {
        val response: ResponseEntity<Array<HealthData>> = restTemplate.getForEntity("/health-data", Array<HealthData>::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Retrieved ${response.body?.size ?: 0} health data entries")
    }

    @Test
    fun `GET health-data should return empty list when no health data exist`() {
        val response: ResponseEntity<Array<HealthData>> = restTemplate.getForEntity("/health-data", Array<HealthData>::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `POST health-data should create health data`() {
        // First create a patient
        val patient = Patient(
            first_name = "Patient",
            second_name = "Test",
            country = "Germany"
        )
        val patientResponse: ResponseEntity<Patient> = restTemplate.postForEntity("/patients", patient, Patient::class.java)
        assert(patientResponse.statusCode == HttpStatus.CREATED)
        val savedPatient = patientResponse.body!!

        val healthData = HealthData(
            patient = savedPatient,
            date = LocalDate.now(),
            size = 180.0,
            weight = 75.0,
            blood_count = "A+ve",
            blood_pressure = "120/80",
            urine_sample = "Normal",
            bloodType = "A+",
            allergies = "None"
        )
        val response: ResponseEntity<HealthData> = restTemplate.postForEntity("/health-data", healthData, HealthData::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.bloodType == "A+")
        println("Created health data: ${response.body?.bloodType}")
    }
}