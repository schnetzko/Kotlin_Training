package com.medical.management

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["spring.profiles.active=test"])
class DemoControllerIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `GET root should return welcome message`() {
        val response = restTemplate.getForEntity("/", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == "Welcome to Medical Management API")
        println("Root endpoint: ${response.body}")
    }
}