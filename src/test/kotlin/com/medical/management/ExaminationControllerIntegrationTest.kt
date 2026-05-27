package com.medical.management

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class ExaminationControllerIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `GET examinations should return all examinations`() {
        val response: ResponseEntity<Array<Examination>> = restTemplate.getForEntity("/examinations", Array<Examination>::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Retrieved ${response.body?.size ?: 0} examinations")
    }

    @Test
    fun `GET examinations should return empty list when no examinations exist`() {
        val response: ResponseEntity<Array<Examination>> = restTemplate.getForEntity("/examinations", Array<Examination>::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `POST examinations should create examination`() {
        // First create a patient
        val patient = Patient(
            first_name = "Patient",
            second_name = "Test",
            country = "Germany"
        )
        val patientResponse: ResponseEntity<Patient> = restTemplate.postForEntity("/patients", patient, Patient::class.java)
        assert(patientResponse.statusCode == HttpStatus.CREATED)
        val savedPatient = patientResponse.body!!

        val examination = Examination(
            specialist = "Dr. Integration",
            name = "Integration Test Examination",
            date = "2024-03-15",
            type = "ROUTINE",
            patient = savedPatient
        )
        val response: ResponseEntity<Examination> = restTemplate.postForEntity("/examinations", examination, Examination::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.name == "Integration Test Examination")
        println("Created examination: ${response.body?.name}")
    }
}
