# Medical Management Backend

This project is a RESTful API built with Kotlin and Spring Boot.

> **Note:** This is a prototype project for demonstration purposes. It is not production-ready and may lack proper error handling, security measures, and other production considerations.

## Tech Stack
- **Language**: Kotlin 1.9.20
- **Framework**: Spring Boot 3.2.0
- **Build Tool**: Gradle
- **Java Version**: 21

## Prerequisites
- Java 21 or higher
- Gradle (or use the included Gradle wrapper)
- PostgreSQL database (for full functionality)

## Running the Application

### Using Gradle Wrapper
```bash
./gradlew bootRun
```

### Using Gradle
```bash
gradle bootRun
```

The application will start on port 8080 by default.

## Running Unit Tests

### Run All Tests
```bash
./gradlew test
```

### Run Tests with Build
```bash
./gradlew build
```

### Run Specific Test Class
```bash
./gradlew test --tests "com.medical.management.PatientControllerTest"
```

### Skip Tests During Build
```bash
./gradlew build -x test
```

### Database Configuration
The application is configured to connect to a PostgreSQL database with the following settings:
- **URL**: `jdbc:postgresql://localhost:5432/medical_data`
- **Username**: `admin`
- **Password**: `medical_pw`

To run without a database, you can modify `src/main/resources/application.yml` to use an in-memory H2 database or set `spring.jpa.hibernate.ddl-auto=none`.

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
