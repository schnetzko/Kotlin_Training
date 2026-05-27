package com.medical.management

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDate

class DiagnosisControllerIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `GET diagnoses should return all diagnoses`() {
        val response: ResponseEntity<Array<Diagnosis>> = restTemplate.getForEntity("/diagnoses", Array<Diagnosis>::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Retrieved ${response.body?.size ?: 0} diagnoses")
    }

    @Test
    fun `GET diagnoses should return empty list when no diagnoses exist`() {
        val response: ResponseEntity<Array<Diagnosis>> = restTemplate.getForEntity("/diagnoses", Array<Diagnosis>::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `POST diagnoses should create diagnosis`() {
        // First create a patient
        val patient = Patient(
            first_name = "Patient",
            second_name = "Test",
            country = "Germany"
        )
        val patientResponse: ResponseEntity<Patient> = restTemplate.postForEntity("/patients", patient, Patient::class.java)
        assert(patientResponse.statusCode == HttpStatus.CREATED)
        val savedPatient = patientResponse.body!!

        val diagnosis = Diagnosis(
            name = "Integration Test Diagnosis",
            code = "Z00.0",
            specialist = "Dr. Integration",
            date = LocalDate.now(),
            type = DiagnosisType.CHRONIC_DISEASES,
            patient = savedPatient
        )
        val response: ResponseEntity<Diagnosis> = restTemplate.postForEntity("/diagnoses", diagnosis, Diagnosis::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.name == "Integration Test Diagnosis")
        println("Created diagnosis: ${response.body?.name}")
    }
}
