package com.medical.management

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Patient(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val first_name: String,
    val second_name: String,
    val mailing_address: String? = null,
    val zip_code: String? = null,
    val city: String? = null,
    val country: String,
    val phone: String? = null,
    val date_of_birth: java.time.LocalDate? = null,

    @OneToOne(mappedBy = "patient")
    val healthInsurance: HealthInsurance? = null,

    @OneToMany(mappedBy = "patient")
    val healthData: List<HealthData> = mutableListOf(),

    @OneToMany(mappedBy = "patient")
    val examinations: List<Examination> = mutableListOf(),

    @OneToMany(mappedBy = "patient")
    val diagnoses: List<Diagnosis> = mutableListOf(),

    @OneToMany(mappedBy = "patient")
    val treatments: List<Treatment> = mutableListOf()
) {
    val allSpecialists: List<String>
        get() = (examinations.map { it.specialist } +
                 diagnoses.map { it.specialist } +
                 treatments.map { it.specialist }).distinct()
}
