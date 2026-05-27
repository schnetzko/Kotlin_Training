package com.medical.management

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
data class HealthInsurance(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val insuranceNumber: String,
    @ManyToOne
    @JsonIgnoreProperties(ignoreUnknown = true)
    val contact: Contact,
    @Enumerated(EnumType.STRING)
    val coverage: Coverage,
    @OneToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties("examinations", "diagnoses", "treatments", "healthData", "healthInsurance", "allSpecialists")
    val patient: Patient? = null
)
