package com.medical.management

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class ContactController(
    private val repository: ContactRepository
) {
    @GetMapping("/")
    fun root() = "Welcome to Medical Management API"

    @GetMapping("/contacts")
    fun getContacts() = repository.findByType("CONTACT")

    @PostMapping("/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    fun createContact(
        @RequestBody contact: Contact
    ) = repository.save(contact.copy(type = "CONTACT"))

    @GetMapping("/specialists")
    fun getSpecialists() = repository.findByType("SPECIALIST")

    @PostMapping("/specialists")
    @ResponseStatus(HttpStatus.CREATED)
    fun createSpecialist(
        @RequestBody contact: Contact
    ) = repository.save(contact.copy(type = "SPECIALIST"))
}
