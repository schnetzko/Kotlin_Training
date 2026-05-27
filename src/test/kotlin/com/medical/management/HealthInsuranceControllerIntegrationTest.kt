package com.medical.management

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.profiles.active=test"])
class HealthInsuranceControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `GET health-insurances should return all health insurances`() {
        val response: ResponseEntity<Array<HealthInsurance>> = restTemplate.getForEntity("/health-insurances", Array<HealthInsurance>::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Retrieved ${response.body?.size ?: 0} health insurances")
    }

    @Test
    fun `GET health-insurances should return empty list when no health insurances exist`() {
        val response: ResponseEntity<Array<HealthInsurance>> = restTemplate.getForEntity("/health-insurances", Array<HealthInsurance>::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `POST health-insurances should create health insurance`() {
        // First create a contact
        val contact = Contact(
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
        val contactResponse: ResponseEntity<Contact> = restTemplate.postForEntity("/contacts", contact, Contact::class.java)
        assert(contactResponse.statusCode == HttpStatus.CREATED)
        val savedContact = contactResponse.body!!

        // Then create a patient
        val patient = Patient(
            first_name = "Patient",
            second_name = "Test",
            country = "Germany"
        )
        val patientResponse: ResponseEntity<Patient> = restTemplate.postForEntity("/patients", patient, Patient::class.java)
        assert(patientResponse.statusCode == HttpStatus.CREATED)
        val savedPatient = patientResponse.body!!

        // Now create health insurance with the saved entities
        val healthInsurance = HealthInsurance(
            name = "Integration Test Insurance",
            insuranceNumber = "IT999999",
            contact = savedContact,
            coverage = Coverage.BY_LAW,
            patient = savedPatient
        )
        val response: ResponseEntity<HealthInsurance> = restTemplate.postForEntity("/health-insurances", healthInsurance, HealthInsurance::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.name == "Integration Test Insurance")
        println("Created health insurance: ${response.body?.name}")
    }
}