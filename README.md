# Medical Management Backend

This project is a RESTful API built with Kotlin and Spring Boot.

## Tech Stack
- **Language**: Kotlin 1.9.0
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Gradle

## Endpoints

### Patients
- `GET /patients`: Retrieve all patients.
- `POST /patients`: Create a new patient.

### Contacts
- `GET /contacts`: Retrieve all contacts.
- `POST /contacts`: Create a new contact.
- `GET /specialists`: Retrieve all specialists.
- `POST /specialists`: Create a new specialist.

### Diagnoses
- `GET /diagnoses`: Retrieve all diagnoses.
- `POST /diagnoses`: Create a new diagnosis.

### Drug Therapies
- `GET /drug-therapies`: Retrieve all drug therapies.
- `POST /drug-therapies`: Create a new drug therapy.

### Examinations
- `GET /examinations`: Retrieve all examinations.
- `POST /examinations`: Create a new examination.

### Health Data
- `GET /health-data`: Retrieve all health data.
- `POST /health-data`: Create new health data.

### Health Insurances
- `GET /health-insurances`: Retrieve all health insurances.
- `POST /health-insurances`: Create a new health insurance.

### Treatments
- `GET /treatments`: Retrieve all treatments.
- `POST /treatments`: Create a new treatment.

## Data Model
The core entity is the `Patient`, which has relationships with:
- `HealthInsurance` (One-to-One)
- `HealthData` (One-to-Many)
- `Examination` (One-to-Many)
- `Diagnosis` (One-to-Many)
- `Treatment` (One-to-Many)
