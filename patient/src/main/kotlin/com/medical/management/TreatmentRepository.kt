package com.medical.management

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TreatmentRepository : JpaRepository<Treatment, Long>
