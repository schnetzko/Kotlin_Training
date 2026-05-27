package com.medical.management

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import java.time.LocalDate

enum class DiagnosisType {
    COMMON_AILMENTS,
    INFECTIOUS,
    CHRONIC_DISEASES,
    MENTAL_AND_NEUROLOGICAL_HEALTH_CONDITIONS,
    INJURIES
}

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
data class Diagnosis(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val code: String,
    val specialist: String,
    val date: LocalDate,
    val type: DiagnosisType,
    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties("examinations", "diagnoses", "treatments", "healthData", "healthInsurance", "allSpecialists")
    val patient: Patient
)
