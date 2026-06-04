package com.medical.management

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
class ContactController(
    private val repository: ContactRepository
) {
    private val logger = LoggerFactory.getLogger(ContactController::class.java)

    @GetMapping("/contacts")
    fun getContacts(): List<Contact> {
        logger.info("GET /contacts - fetching all contacts")
        val contacts = repository.findByType("CONTACT")
        logger.debug("GET /contacts - returning {} contact(s)", contacts.size)
        return contacts
    }

    @PostMapping("/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    fun createContact(
        @RequestBody contact: Contact
    ): Contact {
        logger.info("POST /contacts - creating contact: {}", contact)
        val saved = repository.save(contact.copy(type = "CONTACT"))
        logger.debug("POST /contacts - contact created with id={}", saved.id)
        return saved
    }

    @GetMapping("/specialists")
    fun getSpecialists(): List<Contact> {
        logger.info("GET /specialists - fetching all specialists")
        val specialists = repository.findByType("SPECIALIST")
        logger.debug("GET /specialists - returning {} specialist(s)", specialists.size)
        return specialists
    }

    @PostMapping("/specialists")
    @ResponseStatus(HttpStatus.CREATED)
    fun createSpecialist(
        @RequestBody contact: Contact
    ): Contact {
        logger.info("POST /specialists - creating specialist: {}", contact)
        val saved = repository.save(contact.copy(type = "SPECIALIST"))
        logger.debug("POST /specialists - specialist created with id={}", saved.id)
        return saved
    }
}
