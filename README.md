# Medical Management Backend

A RESTful API built with Kotlin and Spring Boot for general practice. It manages patients and calculates a risk-based prognosis, but also provides management of examinations, diagnosis and treatments by general practitioner or specialists. 

> **Note:** Prototype for demonstration purposes — not production-ready.

## Tech Stack

| | |
|---|---|
| Language | Kotlin 1.9.20 |
| Framework | Spring Boot 3.2.0 |
| Build Tool | Gradle |
| Java Version | 21 |

## Prerequisites

- Java 21+
- Docker (required for local PostgreSQL containers and integration tests)

## Microservices

This repository uses a monorepo structure with separate Spring Boot modules for each domain service.
Each service has its own PostgreSQL database instance and is intended to run independently on a dedicated port. The provided microservices are patient, examination, diagnosis and treatment.

## Data Model

Core entity: `Patient`, related to:
- `HealthInsurance` (One-to-One)
- `HealthData`, `Examination`, `Diagnosis`, `Treatment` (One-to-Many)

## Endpoints

| Resource | GET | POST |
|---|---|---|
| Patients | `GET /patients` | `POST /patients` |
| Contacts | `GET /contacts`, `GET /specialists` | `POST /contacts`, `POST /specialists` |
| Diagnoses | `GET /diagnoses` | `POST /diagnoses` |
| Drug Therapies | `GET /drug-therapies` | `POST /drug-therapies` |
| Examinations | `GET /examinations` | `POST /examinations` |
| Health Data | `GET /health-data` | `POST /health-data` |
| Health Insurances | `GET /health-insurances` | `POST /health-insurances` |
| Treatments | `GET /treatments` | `POST /treatments` |

## Dev Environment

To simplify local development there are helper scripts in admin/dev directory that manage local PostgreSQL containers using Docker Compose and application lifecycle.

```bash
/admin/dev/start.sh # starts all microservices (service = 1 JVM process and 1 Docker container)

/admin/dev/stop.sh # stops all microservices.

/admin/dev/restart.sh # restarts all microservices.
```

Or run each microservice separately for debugging or running tests.

```bash
# 1. start PostgreSQL containers
./docker compose -up # (not required for tests)

# 2. start Spring Boot service
./gradlew <microservice>:bootRun|bootRunDebug
# example for microservice patient 
./gradlew patient:bootRun
./gradlew patient:bootRunDebug
```

### local Endpoints

- Patient http://localhost:8081/
- Treatment http://localhost:8082/
- Diagnosis http://localhost:8083/
- Examination http://localhost:8084/

### Running Tests

Integration tests (`*IntegrationTest.kt`) use **Testcontainers** to spin up a real `postgres:16-alpine` container automatically — no manual setup needed. Schema is created/dropped via `spring.jpa.hibernate.ddl-auto: create-drop` (see [`application-test.yml`](src/test/resources/application-test.yml)).

| Task | What it runs | Docker required |
|---|---|---|
| `./gradlew <service>:unitTest` | Unit tests only (`*Test.kt`, excluding `*IntegrationTest.kt`) | No |
| `./gradlew <service>:integrationTest` | Integration tests only (`*IntegrationTest.kt`) | Yes |
| `./gradlew <service>:test` | All tests (unit + integration) | Yes |
| `./gradlew <service>:build` | Full build including all tests and linting | Yes |

```bash
# example for microservice patient
./gradlew patient:unitTest
./gradlew patient:integrationTest
./gradlew patient:test                                                        # all tests
./gradlew patient:test --tests "com.medical.management.PatientControllerTest" # specific test class
./gradlew patient:build
./gradlew patient:build -x test                                               # build without running tests
```

### Debugging in VS Code

The project ships with pre-configured launch configs ([`.vscode/launch.json`](.vscode/launch.json)) and tasks ([`.vscode/tasks.json`](.vscode/tasks.json)).

Open **Run & Debug** (`Ctrl+Shift+D`) and select a configuration:

| Configuration | Description | Port | Suspend |
|---|---|---|---|
| `Kotlin: Boot Run + Attach Debugger` *(recommended)* | Starts app in debug mode and auto-attaches | `5005` | `n` |
| `Kotlin: Attach to Spring Boot (5005)` | Attaches to an already-running `./gradlew bootRunDebug` instance | `5005` | `n` |
| `Kotlin: Run DemoApplication (Java debugger)` | Compiles then launches via VS Code Java debugger | — | `n` |
| `Kotlin: Debug Unit Tests` | Runs unit tests suspended until debugger connects | `5006` | `y` |
| `Kotlin: Debug Integration Tests` | Runs integration tests suspended until debugger connects (requires Docker) | `5007` | `y` |

> **Remark:** An `.env` file must exist in the workspace root if environment variables are required.

### Code Coverage

Coverage is measured with **[JaCoCo](https://www.jacoco.org/jacoco/)** 0.8.11. Three separate HTML + XML reports are generated:

| Task | Tests | Report location |
|---|---|---|
| `./gradlew unitTest jacocoUnitTestReport` | Unit only | `build/reports/jacoco/unitTest/html/index.html` |
| `./gradlew integrationTest jacocoIntegrationTestReport` | Integration only | `build/reports/jacoco/integrationTest/html/index.html` |
| `./gradlew jacocoCombinedReport` | Unit + integration | `build/reports/jacoco/combined/html/index.html` |

XML reports (same paths, `.xml` extension) are compatible with SonarQube, Codecov, and standard CI pipelines.

**Excluded from coverage:** `*ApplicationKt` (entry point) and Kotlin synthetic classes (`*$*.class`).

**VS Code tasks** (`Ctrl+Shift+P` → *Tasks: Run Task*): `gradle: coverage (unit tests)`, `gradle: coverage (integration tests)`, `gradle: coverage (combined)`, `coverage: open unit test report`, `coverage: open combined report`.

### Linting

Code style is enforced with **[ktlint](https://pinterest.github.io/ktlint/)** 1.3.1 via the `org.jlleitschuh.gradle.ktlint` plugin 12.1.1. Project-specific overrides are in [`.editorconfig`](.editorconfig).

```bash
./gradlew ktlintCheck    # check for violations (CI-safe, non-zero exit on failure)
./gradlew ktlintFormat   # auto-fix violations in-place
```

`ktlintCheck` runs automatically as part of `./gradlew check` and `./gradlew build`. To skip: `./gradlew build -x ktlintCheck`.

Key style rules (see [`.editorconfig`](.editorconfig) for full config):

| Rule | Value |
|---|---|
| Indent style | spaces (4) |
| Max line length | 120 |
| Wildcard imports | disabled |
| Trailing commas | disabled |

Reports are written to `build/reports/ktlint/` (Checkstyle-compatible XML).
