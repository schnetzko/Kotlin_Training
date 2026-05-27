# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.1.0-dev] - 2026-05-27

> ⚠️ This release is **not production-ready**. It is a development snapshot intended for prototyping and demonstration purposes only. It may lack proper error handling, security measures, and other production considerations.

### Added

#### Sickness Prognosis (first feature)
- Introduced [`SicknessPrognosis`](src/main/kotlin/com/medical/management/SicknessPrognosis.kt) data class representing a patient's prognosis result, including `patientId`, `riskLevel`, `potentialConditions`, and `recommendations`.
- Introduced [`RiskLevel`](src/main/kotlin/com/medical/management/SicknessPrognosis.kt) enum with values `LOW`, `MODERATE`, and `HIGH`.
- Introduced [`SicknessPrognosisService`](src/main/kotlin/com/medical/management/SicknessPrognosisService.kt) that calculates a risk-based prognosis for a given patient by analysing the latest available health data record:
  - **BMI analysis** — detects obesity, overweight, and underweight conditions.
  - **Blood pressure analysis** — detects Stage 1/2 hypertension and hypotension.
  - **Blood count analysis** — flags anaemia or blood disorders based on keyword matching.
  - **Urine sample analysis** — flags kidney disease or Diabetes Mellitus indicators.
  - **Allergy risk** — raises risk score when known allergies are present.
  - Returns `422 Unprocessable Entity` when no health data is available for the requested patient.

#### Core REST API
- [`PatientController`](src/main/kotlin/com/medical/management/PatientController.kt) — CRUD endpoints for patient management.
- [`ContactController`](src/main/kotlin/com/medical/management/ContactController.kt) — CRUD endpoints for patient contact information.
- [`DiagnosisController`](src/main/kotlin/com/medical/management/DiagnosisController.kt) — CRUD endpoints for patient diagnoses.
- [`DrugTherapyController`](src/main/kotlin/com/medical/management/DrugTherapyController.kt) — CRUD endpoints for drug therapy records.
- [`ExaminationController`](src/main/kotlin/com/medical/management/ExaminationController.kt) — CRUD endpoints for patient examinations.
- [`HealthDataController`](src/main/kotlin/com/medical/management/HealthDataController.kt) — CRUD endpoints for patient health data (weight, height, blood pressure, blood count, urine sample, allergies).
- [`HealthInsuranceController`](src/main/kotlin/com/medical/management/HealthInsuranceController.kt) — CRUD endpoints for health insurance records.
- [`TreatmentController`](src/main/kotlin/com/medical/management/TreatmentController.kt) — CRUD endpoints for patient treatments.

#### Infrastructure & Testing
- Spring Boot 3.2.0 application with Kotlin 1.9.20 and Java 21.
- PostgreSQL persistence via Spring Data JPA.
- Separate Gradle tasks `unitTest` and `integrationTest` for isolated test execution.
- Integration tests using **Testcontainers** (`postgres:16-alpine`) with full Spring MVC stack via `MockMvc`.
- [`PatientIntegrationTest`](src/test/kotlin/com/medical/management/PatientIntegrationTest.kt) as the reference end-to-end integration test.
- VS Code launch configurations for one-click debug attach (`.vscode/launch.json`, `.vscode/tasks.json`).
- `.editorconfig` for consistent code style across editors.

---

<!-- Links -->
[0.1.0-dev]: https://github.com/your-org/your-repo/releases/tag/v0.1.0-dev
