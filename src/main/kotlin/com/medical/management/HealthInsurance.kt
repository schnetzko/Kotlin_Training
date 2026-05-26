package com.medical.management

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn

@Entity
data class HealthInsurance(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val insuranceNumber: String,
    @ManyToOne
    val contact: Contact,
    @Enumerated(EnumType.STRING)
    val coverage: Coverage,
    @OneToOne
    @JoinColumn(name = "patient_id")
    val patient: Patient? = null
)
