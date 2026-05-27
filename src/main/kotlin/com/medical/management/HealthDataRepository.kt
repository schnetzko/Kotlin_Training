package com.medical.management

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HealthDataRepository : JpaRepository<HealthData, Long> {
    fun findByPatientId(patientId: Long): List<HealthData>
}
