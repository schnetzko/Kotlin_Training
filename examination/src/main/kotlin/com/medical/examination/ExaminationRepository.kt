package com.medical.examination

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExaminationRepository : JpaRepository<Examination, Long>
