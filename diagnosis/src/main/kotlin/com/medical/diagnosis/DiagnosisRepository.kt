package com.medical.diagnosis

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DiagnosisRepository : JpaRepository<Diagnosis, Long>
