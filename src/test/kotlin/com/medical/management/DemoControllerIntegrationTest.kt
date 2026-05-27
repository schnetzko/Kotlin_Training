package com.medical.management

import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class DemoControllerIntegrationTest : AbstractIntegrationTest() {

    @Test
    fun `GET root should return welcome message`() {
        val response = restTemplate.getForEntity("/", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == "Welcome to Medical Management API")
        println("Root endpoint: ${response.body}")
    }
}
