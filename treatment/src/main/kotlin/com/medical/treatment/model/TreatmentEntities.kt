package com.medical.treatment.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*


/** cached copy of partly Patient entity, 
 * event driven synchronization via Kafka with Patient endpoint */
@Entity
@Table(name = "cached_patients")
class CachedPatient(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @Column(name = "first_name", nullable = false)
    val first_name: String,

    @Column(name = "second_name", nullable = false)
    val second_name: String,

    @Column(name = "mailing_address", nullable = true)
    val mailing_address: String? = null,

    @Column(name = "zip_code", nullable = true)
    val zip_code: String? = null,

    @Column(name = "city", nullable = true)
    val city: String? = null,

    @Column(name = "country", nullable = false)
    val country: String,
    
    @Column(name = "phone", nullable = true)
    val phone: String? = null,
    
    @Column(name = "date_of_birth", nullable = true)
    val date_of_birth: java.time.LocalDate? = null,
    
    // bidirectional: allows loading of all treatments directly via its patient object and reverse
    @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val treatments: MutableList<Treatment> = mutableListOf()
)

@Entity
@Table(name = "treatments")
class Treatment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val specialist: String,
    val name: String,
    val date: String,
    val type: String,

    // each treatment belongs to exact on patient
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    val patient: CachedPatient
)