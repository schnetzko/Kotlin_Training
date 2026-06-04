package com.medical.management

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
data class DrugTherapy(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val drugName: String,
    val description: String,
    val activeIngredient: String,
    val dosage: String,
    val frequence: String
)
