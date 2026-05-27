package com.medical.management

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ContactController::class)
class ContactControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var repository: ContactRepository

    private val contact1 = Contact(
        id = 1L,
        firstName = "John",
        secondName = "Doe",
        mailingAddress = "123 Main St",
        zipCode = "12345",
        city = "Berlin",
        country = "Germany",
        phone = "123456789",
        email = "john.doe@example.com",
        type = "CONTACT"
    )

    private val contact2 = Contact(
        id = 2L,
        firstName = "Jane",
        secondName = "Smith",
        mailingAddress = "456 Oak Ave",
        zipCode = "67890",
        city = "Munich",
        country = "Germany",
        phone = "987654321",
        email = "jane.smith@example.com",
        type = "CONTACT"
    )

    private val specialist = Contact(
        id = 3L,
        firstName = "Dr. Hans",
        secondName = "Gruber",
        mailingAddress = "789 Doctor St",
        zipCode = "11111",
        city = "Vienna",
        country = "Austria",
        phone = "111222333",
        email = "dr.gruber@example.com",
        type = "SPECIALIST"
    )

    @BeforeEach
    fun setup() {
        reset(repository)
    }

    @Test
    fun `GET root should return welcome message`() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string("Welcome to Medical Management API"))
    }

    @Test
    fun `GET contacts should return all contacts`() {
        `when`(repository.findByType("CONTACT")).thenReturn(listOf(contact1, contact2))

        mockMvc.perform(get("/contacts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].firstName").value("John"))
            .andExpect(jsonPath("$.[1].firstName").value("Jane"))

        verify(repository).findByType("CONTACT")
    }

    @Test
    fun `GET contacts should return empty list when no contacts`() {
        `when`(repository.findByType("CONTACT")).thenReturn(emptyList())

        mockMvc.perform(get("/contacts"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(emptyList<Any>())))

        verify(repository).findByType("CONTACT")
    }

    @Test
    fun `POST contacts should create contact`() {
        val newContact = Contact(
            firstName = "New",
            secondName = "Contact",
            mailingAddress = "New Address",
            zipCode = "99999",
            city = "Berlin",
            country = "Germany",
            phone = "555555555",
            email = "new.contact@example.com",
            type = "CONTACT"
        )

        `when`(repository.save(any(Contact::class.java))).thenAnswer { invocation ->
            val saved = invocation.arguments[0] as Contact
            saved.copy(id = 100L, type = "CONTACT")
        }

        mockMvc.perform(post("/contacts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newContact)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("New"))
            .andExpect(jsonPath("$.type").value("CONTACT"))

        verify(repository).save(any(Contact::class.java))
    }

    @Test
    fun `GET specialists should return all specialists`() {
        `when`(repository.findByType("SPECIALIST")).thenReturn(listOf(specialist))

        mockMvc.perform(get("/specialists"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].firstName").value("Dr. Hans"))
            .andExpect(jsonPath("$.[0].type").value("SPECIALIST"))

        verify(repository).findByType("SPECIALIST")
    }

    @Test
    fun `POST specialists should create specialist`() {
        val newSpecialist = Contact(
            firstName = "Dr. New",
            secondName = "Doctor",
            mailingAddress = "Doctor Address",
            zipCode = "88888",
            city = "Frankfurt",
            country = "Germany",
            phone = "444444444",
            email = "dr.new@example.com",
            type = "SPECIALIST"
        )

        `when`(repository.save(any(Contact::class.java))).thenAnswer { invocation ->
            val saved = invocation.arguments[0] as Contact
            saved.copy(id = 200L, type = "SPECIALIST")
        }

        mockMvc.perform(post("/specialists")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newSpecialist)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("Dr. New"))
            .andExpect(jsonPath("$.type").value("SPECIALIST"))

        verify(repository).save(any(Contact::class.java))
    }
}