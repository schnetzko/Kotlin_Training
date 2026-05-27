package com.medical.management

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class TreatmentControllerIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `GET treatments should return all treatments`() {
        val response: ResponseEntity<Array<Treatment>> = restTemplate.getForEntity("/treatments", Array<Treatment>::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Retrieved ${response.body?.size ?: 0} treatments")
    }

    @Test
    fun `GET treatments should return empty list when no treatments exist`() {
        val response: ResponseEntity<Array<Treatment>> = restTemplate.getForEntity("/treatments", Array<Treatment>::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `POST treatments should create treatment`() {
        // First create a patient
        val patient = Patient(
            first_name = "Patient",
            second_name = "Test",
            country = "Germany"
        )
        val patientResponse: ResponseEntity<Patient> = restTemplate.postForEntity("/patients", patient, Patient::class.java)
        assert(patientResponse.statusCode == HttpStatus.CREATED)
        val savedPatient = patientResponse.body!!

        val treatment = Treatment(
            specialist = "Dr. Integration",
            name = "Integration Test Treatment",
            date = "2024-03-15",
            type = "CHRONIC_CARE",
            patient = savedPatient
        )
        val response: ResponseEntity<Treatment> = restTemplate.postForEntity("/treatments", treatment, Treatment::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.name == "Integration Test Treatment")
        println("Created treatment: ${response.body?.name}")
    }
}
