package com.medical.management

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
data class HealthData(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties("examinations", "diagnoses", "treatments", "healthData", "healthInsurance", "allSpecialists")
    val patient: Patient,
    val date: LocalDate,
    val size: Double,
    val weight: Double,
    val blood_count: String,
    val blood_pressure: String,
    val urine_sample: String,
    val bloodType: String,
    val allergies: String
)
