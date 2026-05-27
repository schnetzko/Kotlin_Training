package com.medical.management

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class SicknessPrognosisService(
    private val healthDataRepository: HealthDataRepository
) {
    fun calculatePrognosis(patientId: Long): SicknessPrognosis {
        val records = healthDataRepository.findByPatientId(patientId)

        if (records.isEmpty()) {
            throw ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "No complete health data available for patient $patientId"
            )
        }

        val latest = records.maxByOrNull { it.date }
            ?: throw ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "No complete health data available for patient $patientId"
            )

        val potentialConditions = mutableListOf<String>()
        val recommendations = mutableListOf<String>()
        var riskScore = 0

        // BMI analysis
        val bmi = latest.weight / ((latest.size / 100) * (latest.size / 100))
        when {
            bmi >= 30.0 -> {
                riskScore += 2
                potentialConditions.add("Obesity-related conditions (Type 2 Diabetes, Hypertension)")
                recommendations.add("Consult a nutritionist and adopt a calorie-controlled diet")
            }
            bmi >= 25.0 -> {
                riskScore += 1
                potentialConditions.add("Metabolic syndrome risk")
                recommendations.add("Increase physical activity and monitor weight regularly")
            }
            bmi < 18.5 -> {
                riskScore += 1
                potentialConditions.add("Malnutrition or eating disorder risk")
                recommendations.add("Consult a dietitian for a balanced nutrition plan")
            }
            else -> { /* healthy BMI range – no action */ }
        }

        // Blood pressure analysis (format: "systolic/diastolic")
        parseSystolicDiastolic(latest.blood_pressure)?.let { (systolic, diastolic) ->
            when {
                systolic >= 140 || diastolic >= 90 -> {
                    riskScore += 2
                    potentialConditions.add("Hypertension (Stage 2)")
                    recommendations.add("Seek immediate medical evaluation for high blood pressure")
                }
                systolic in 130..139 || diastolic in 80..89 -> {
                    riskScore += 1
                    potentialConditions.add("Hypertension (Stage 1)")
                    recommendations.add("Reduce sodium intake and monitor blood pressure regularly")
                }
                systolic < 90 || diastolic < 60 -> {
                    riskScore += 1
                    potentialConditions.add("Hypotension")
                    recommendations.add("Increase fluid intake and consult a physician")
                }
                else -> { /* normal blood pressure – no action */ }
            }
        }

        // Blood count analysis
        val bloodCountLower = latest.blood_count.lowercase()
        if (bloodCountLower.contains("low") ||
            bloodCountLower.contains("anemia") ||
            bloodCountLower.contains("abnormal")
        ) {
            riskScore += 2
            potentialConditions.add("Anemia or blood disorder")
            recommendations.add("Schedule a complete blood panel with a hematologist")
        }

        // Urine sample analysis
        val urineLower = latest.urine_sample.lowercase()
        if (urineLower.contains("abnormal") ||
            urineLower.contains("protein") ||
            urineLower.contains("glucose") ||
            urineLower.contains("blood")
        ) {
            riskScore += 2
            potentialConditions.add("Kidney disease or Diabetes Mellitus")
            recommendations.add("Consult a nephrologist or endocrinologist for further evaluation")
        }

        // Allergy risk
        val allergiesLower = latest.allergies.lowercase()
        if (allergiesLower != "none" && allergiesLower.isNotBlank()) {
            riskScore += 1
            potentialConditions.add("Allergic reactions or anaphylaxis risk")
            recommendations.add("Carry an emergency allergy kit and inform all treating physicians")
        }

        if (potentialConditions.isEmpty()) {
            potentialConditions.add("No significant risk factors detected")
            recommendations.add("Maintain current healthy lifestyle and schedule annual check-ups")
        }

        val riskLevel = when {
            riskScore >= 4 -> RiskLevel.HIGH
            riskScore >= 2 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }

        return SicknessPrognosis(
            patientId = patientId,
            riskLevel = riskLevel,
            potentialConditions = potentialConditions.distinct(),
            recommendations = recommendations.distinct()
        )
    }

    private fun parseSystolicDiastolic(bloodPressure: String): Pair<Int, Int>? {
        val parts = bloodPressure.split("/")
        if (parts.size != 2) return null
        return try {
            Pair(parts[0].trim().toInt(), parts[1].trim().toInt())
        } catch (e: NumberFormatException) {
            null
        }
    }
}
