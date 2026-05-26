package com.medical.management

import org.springframework.web.bind.annotation.*

@RestController
class ContactController(private val repository: ContactRepository) {
    @GetMapping("/contacts")
    fun getContacts() = repository.findByType("CONTACT")

    @PostMapping("/contacts")
    fun createContact(@RequestBody contact: Contact) = repository.save(contact.copy(type = "CONTACT"))

    @GetMapping("/specialists")
    fun getSpecialists() = repository.findByType("SPECIALIST")

    @PostMapping("/specialists")
    fun createSpecialist(@RequestBody contact: Contact) = repository.save(contact.copy(type = "SPECIALIST"))
}
