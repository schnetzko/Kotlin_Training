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

#### Logging

Every controller uses SLF4J via [`LoggerFactory`](src/main/kotlin/com/medical/management/PatientController.kt:17) with a two-level logging strategy applied consistently across all endpoints:

- **`INFO`** — logged on every incoming request, capturing the HTTP method, path, and (for write operations) the full request payload.
- **`DEBUG`** — logged after the operation completes, capturing the result count or the generated entity `id`.

The table below lists each endpoint and its exact log messages:

| Controller | Endpoint | `INFO` message | `DEBUG` message |
|---|---|---|---|
| [`PatientController`](src/main/kotlin/com/medical/management/PatientController.kt) | `GET /patients` | `GET /patients - fetching all patients` | `GET /patients - returning {n} patient(s)` |
| [`PatientController`](src/main/kotlin/com/medical/management/PatientController.kt) | `POST /patients` | `POST /patients - creating patient: {payload}` | `POST /patients - patient created with id={id}` |
| [`ContactController`](src/main/kotlin/com/medical/management/ContactController.kt) | `GET /` | `GET / - root endpoint called` | — |
| [`ContactController`](src/main/kotlin/com/medical/management/ContactController.kt) | `GET /contacts` | `GET /contacts - fetching all contacts` | `GET /contacts - returning {n} contact(s)` |
| [`ContactController`](src/main/kotlin/com/medical/management/ContactController.kt) | `POST /contacts` | `POST /contacts - creating contact: {payload}` | `POST /contacts - contact created with id={id}` |
| [`ContactController`](src/main/kotlin/com/medical/management/ContactController.kt) | `GET /specialists` | `GET /specialists - fetching all specialists` | `GET /specialists - returning {n} specialist(s)` |
| [`ContactController`](src/main/kotlin/com/medical/management/ContactController.kt) | `POST /specialists` | `POST /specialists - creating specialist: {payload}` | `POST /specialists - specialist created with id={id}` |
| [`DiagnosisController`](src/main/kotlin/com/medical/management/DiagnosisController.kt) | `GET /diagnoses` | `GET /diagnoses - fetching all diagnoses` | `GET /diagnoses - returning {n} diagnosis/diagnoses` |
| [`DiagnosisController`](src/main/kotlin/com/medical/management/DiagnosisController.kt) | `POST /diagnoses` | `POST /diagnoses - creating diagnosis: {payload}` | `POST /diagnoses - diagnosis created with id={id}` |
| [`DrugTherapyController`](src/main/kotlin/com/medical/management/DrugTherapyController.kt) | `GET /drug-therapies` | `GET /drug-therapies - fetching all drug therapies` | `GET /drug-therapies - returning {n} drug therapy/therapies` |
| [`DrugTherapyController`](src/main/kotlin/com/medical/management/DrugTherapyController.kt) | `POST /drug-therapies` | `POST /drug-therapies - creating drug therapy: {payload}` | `POST /drug-therapies - drug therapy created with id={id}` |
| [`ExaminationController`](src/main/kotlin/com/medical/management/ExaminationController.kt) | `GET /examinations` | `GET /examinations - fetching all examinations` | `GET /examinations - returning {n} examination(s)` |
| [`ExaminationController`](src/main/kotlin/com/medical/management/ExaminationController.kt) | `POST /examinations` | `POST /examinations - creating examination: {payload}` | `POST /examinations - examination created with id={id}` |
| [`HealthDataController`](src/main/kotlin/com/medical/management/HealthDataController.kt) | `GET /health-data` | `GET /health-data - fetching all health data` | `GET /health-data - returning {n} health data record(s)` |
| [`HealthDataController`](src/main/kotlin/com/medical/management/HealthDataController.kt) | `POST /health-data` | `POST /health-data - creating health data record: {payload}` | `POST /health-data - health data record created with id={id}` |
| [`HealthDataController`](src/main/kotlin/com/medical/management/HealthDataController.kt) | `GET /health-data/patients/{patientId}/prognosis` | `GET /health-data/patients/{id}/prognosis - calculating prognosis for patientId={id}` | `GET /health-data/patients/{id}/prognosis - prognosis result: {result}` |
| [`HealthInsuranceController`](src/main/kotlin/com/medical/management/HealthInsuranceController.kt) | `GET /health-insurances` | `GET /health-insurances - fetching all health insurances` | `GET /health-insurances - returning {n} health insurance(s)` |
| [`HealthInsuranceController`](src/main/kotlin/com/medical/management/HealthInsuranceController.kt) | `POST /health-insurances` | `POST /health-insurances - creating health insurance: {payload}` | `POST /health-insurances - health insurance created with id={id}` |
| [`TreatmentController`](src/main/kotlin/com/medical/management/TreatmentController.kt) | `GET /treatments` | `GET /treatments - fetching all treatments` | `GET /treatments - returning {n} treatment(s)` |
| [`TreatmentController`](src/main/kotlin/com/medical/management/TreatmentController.kt) | `POST /treatments` | `POST /treatments - creating treatment: {payload}` | `POST /treatments - treatment created with id={id}` |
| [`SicknessPrognosisController`](src/main/kotlin/com/medical/management/SicknessPrognosisController.kt) | `GET /patients/{patientId}/prognosis` | `GET /patients/{id}/prognosis - calculating sickness prognosis` | `GET /patients/{id}/prognosis - riskLevel={riskLevel}` |

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
