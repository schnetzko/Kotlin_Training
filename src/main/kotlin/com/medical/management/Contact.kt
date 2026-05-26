package com.medical.management

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Contact(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val firstName: String,
    val secondName: String,
    val mailingAddress: String,
    val zipCode: String,
    val city: String,
    val country: String,
    val phone: String,
    val email: String,
    val type: String // "CONTACT" or "SPECIALIST"
)
