package com.medical.treatment

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
data class Treatment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val specialist: String,
    val name: String,
    val date: String,
    val type: String,
 /*    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties("examinations", "diagnoses", "treatments", "healthData", "healthInsurance", "allSpecialists")
    val patient: Patient */
)
