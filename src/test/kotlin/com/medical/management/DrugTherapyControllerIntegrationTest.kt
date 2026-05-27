package com.medical.management

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.profiles.active=test"])
class DrugTherapyControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `GET drug-therapies should return all drug therapies`() {
        val response: ResponseEntity<Array<DrugTherapy>> = restTemplate.getForEntity("/drug-therapies", Array<DrugTherapy>::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Retrieved ${response.body?.size ?: 0} drug therapies")
    }

    @Test
    fun `GET drug-therapies should return empty list when no drug therapies exist`() {
        val response: ResponseEntity<Array<DrugTherapy>> = restTemplate.getForEntity("/drug-therapies", Array<DrugTherapy>::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `POST drug-therapies should create drug therapy`() {
        val drugTherapy = DrugTherapy(
            drugName = "Integration Test Drug",
            description = "Test drug for integration testing",
            activeIngredient = "Test Ingredient",
            dosage = "1 tablet",
            frequence = "Once daily"
        )
        val response: ResponseEntity<DrugTherapy> = restTemplate.postForEntity("/drug-therapies", drugTherapy, DrugTherapy::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body?.drugName == "Integration Test Drug")
        println("Created drug therapy: ${response.body?.drugName}")
    }
}