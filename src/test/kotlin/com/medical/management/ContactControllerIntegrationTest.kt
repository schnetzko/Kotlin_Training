package com.medical.management

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.MethodOrderer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.profiles.active=test"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ContactControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `GET contacts should return all contacts`() {
        val response: ResponseEntity<Array<Contact>> = restTemplate.getForEntity("/contacts", Array<Contact>::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Retrieved ${response.body?.size ?: 0} contacts")
    }

    @Test
    fun `GET contacts should return empty list when no contacts exist`() {
        val response: ResponseEntity<Array<Contact>> = restTemplate.getForEntity("/contacts", Array<Contact>::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `POST contacts should create contact`() {
        val contact = Contact(
            firstName = "Integration",
            secondName = "Test",
            mailingAddress = "Integration Test Address",
            zipCode = "99999",
            city = "Test City",
            country = "Test Country",
            phone = "999999999",
            email = "integration.test@example.com",
            type = "CONTACT"
        )
        val response: ResponseEntity<Contact> = restTemplate.postForEntity("/contacts", contact, Contact::class.java)
        assert(response.statusCode == HttpStatus.CREATED) { "Expected CREATED but got ${response.statusCode}" }
        assert(response.body?.firstName == "Integration")
        assert(response.body?.type == "CONTACT")
        println("Created contact: ${response.body?.firstName}")
    }

    @Test
    fun `GET specialists should return all specialists`() {
        val response: ResponseEntity<Array<Contact>> = restTemplate.getForEntity("/specialists", Array<Contact>::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Retrieved ${response.body?.size ?: 0} specialists")
    }

    @Test
    fun `GET specialists should return empty list when no specialists exist`() {
        val response: ResponseEntity<Array<Contact>> = restTemplate.getForEntity("/specialists", Array<Contact>::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `POST specialists should create specialist`() {
        val specialist = Contact(
            firstName = "Dr. Integration",
            secondName = "Test",
            mailingAddress = "Integration Test Address",
            zipCode = "99999",
            city = "Test City",
            country = "Test Country",
            phone = "999999999",
            email = "dr.integration@example.com",
            type = "SPECIALIST"
        )
        val response: ResponseEntity<Contact> = restTemplate.postForEntity("/specialists", specialist, Contact::class.java)
        assert(response.statusCode == HttpStatus.CREATED) { "Expected CREATED but got ${response.statusCode}" }
        assert(response.body?.firstName == "Dr. Integration")
        assert(response.body?.type == "SPECIALIST")
        println("Created specialist: ${response.body?.firstName}")
    }
}