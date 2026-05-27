package com.medical.management

data class SicknessPrognosis(
    val patientId: Long,
    val riskLevel: RiskLevel,
    val potentialConditions: List<String>,
    val recommendations: List<String>
)

enum class RiskLevel {
    LOW,
    MODERATE,
    HIGH
}
