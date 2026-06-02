# Medical Management Backend

A RESTful API built with Kotlin and Spring Boot.

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

## Running the Application

```bash
./gradlew bootRun   # via Gradle wrapper (recommended)
./gradlew bootRunDebug
```

Default root API port: `8081`.
Local database config: `jdbc:postgresql://localhost:5432/medical_data` (user: `postgres`, password: `postgres`).

## Development scripts

To simplify local development there are helper scripts in the repository root that manage the local PostgreSQL container and application lifecycle.

- `./start.sh` — Ensures the `kotlin_training_postgres` container is running (creates it if missing), waits for readiness, creates the `medical_data` database if needed, then starts the application via `./gradlew bootRun`.
- `./start_debug.sh` — Same as `start.sh` but starts the application in debug mode via `./gradlew bootRunDebug` (debug port 5005).
- `./stop.sh` — Stops the Spring Boot process and the `kotlin_training_postgres` Docker container.
- `./restart.sh` — Runs `./stop.sh` then `./start.sh` to restart services.
- `./restart_debug.sh` — Runs `./stop.sh` then `./start_debug.sh` to restart services in debug mode.
- `./check.sh` — Reports status for the PostgreSQL container, `medical_data` database, the application process, HTTP port (8081) and debug port (5005).

Example:

```bash
./start.sh
# or for debugging
./start_debug.sh

./check.sh
./stop.sh
./restart.sh
./restart_debug.sh
```

## Docker Compose

A local `docker-compose.yml` is included to start one PostgreSQL database instance per service:

- `postgres_treatment` → host port `5433`, DB `treatment_db`
- `postgres_diagnosis` → host port `5434`, DB `diagnosis_db`
- `postgres_examination` → host port `5435`, DB `examination_db`

Run the database stack:

```bash
docker compose up -d
```

Stop it:

```bash
docker compose down
```

Service application ports:

- `services/treatment` → `8082`
- `services/diagnosis` → `8083`
- `services/examination` → `8084`

## Microservices

This repository uses a monorepo structure with separate Spring Boot modules for each domain service.
Each service has its own PostgreSQL database instance and is intended to run independently on a dedicated port:

- `services/treatment` — treatment microservice on port `8082`
- `services/diagnosis` — diagnosis microservice on port `8083`
- `services/examination` — examination microservice on port `8084`

Use `docker compose up -d` to start the per-service PostgreSQL databases, then run each service module independently as needed.

The VS Code tasks in `.vscode/tasks.json` have been updated to call the helper scripts, so you can run them via the Command Palette (`Tasks: Run Task`) as well.

## Running Tests

Integration tests (`*IntegrationTest.kt`) use **Testcontainers** to spin up a real `postgres:16-alpine` container automatically — no manual setup needed. Schema is created/dropped via `spring.jpa.hibernate.ddl-auto: create-drop` (see [`application-test.yml`](src/test/resources/application-test.yml)).

| Task | What it runs | Docker required |
|---|---|---|
| `./gradlew unitTest` | Unit tests only (`*Test.kt`, excluding `*IntegrationTest.kt`) | No |
| `./gradlew integrationTest` | Integration tests only (`*IntegrationTest.kt`) | Yes |
| `./gradlew test` | All tests (unit + integration) | Yes |
| `./gradlew build` | Full build including all tests and linting | Yes |

```bash
./gradlew unitTest                                                          # unit tests only
./gradlew integrationTest                                                   # integration tests only
./gradlew test                                                              # all tests
./gradlew test --tests "com.medical.management.PatientControllerTest"       # specific class
./gradlew build -x test -x integrationTest                                  # skip all tests
```

## Debugging in VS Code

The project ships with pre-configured launch configs ([`.vscode/launch.json`](.vscode/launch.json)) and tasks ([`.vscode/tasks.json`](.vscode/tasks.json)).

Open **Run & Debug** (`Ctrl+Shift+D`) and select a configuration:

| Configuration | Description | Port | Suspend |
|---|---|---|---|
| `Kotlin: Boot Run + Attach Debugger` *(recommended)* | Starts app in debug mode and auto-attaches | `5005` | `n` |
| `Kotlin: Attach to Spring Boot (5005)` | Attaches to an already-running `./gradlew bootRunDebug` instance | `5005` | `n` |
| `Kotlin: Run DemoApplication (Java debugger)` | Compiles then launches via VS Code Java debugger | — | `n` |
| `Kotlin: Debug Unit Tests` | Runs unit tests suspended until debugger connects | `5006` | `y` |
| `Kotlin: Debug Integration Tests` | Runs integration tests suspended until debugger connects (requires Docker) | `5007` | `y` |

> **Option 3 prerequisite:** An `.env` file must exist in the workspace root if environment variables are required.

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

## Code Coverage

Coverage is measured with **[JaCoCo](https://www.jacoco.org/jacoco/)** 0.8.11. Three separate HTML + XML reports are generated:

| Task | Tests | Report location |
|---|---|---|
| `./gradlew unitTest jacocoUnitTestReport` | Unit only | `build/reports/jacoco/unitTest/html/index.html` |
| `./gradlew integrationTest jacocoIntegrationTestReport` | Integration only | `build/reports/jacoco/integrationTest/html/index.html` |
| `./gradlew jacocoCombinedReport` | Unit + integration | `build/reports/jacoco/combined/html/index.html` |

XML reports (same paths, `.xml` extension) are compatible with SonarQube, Codecov, and standard CI pipelines.

**Excluded from coverage:** `DemoApplicationKt` (entry point) and Kotlin synthetic classes (`*$*.class`).

**VS Code tasks** (`Ctrl+Shift+P` → *Tasks: Run Task*): `gradle: coverage (unit tests)`, `gradle: coverage (integration tests)`, `gradle: coverage (combined)`, `coverage: open unit test report`, `coverage: open combined report`.

## Linting

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

## Data Model

Core entity: `Patient`, related to:
- `HealthInsurance` (One-to-One)
- `HealthData`, `Examination`, `Diagnosis`, `Treatment` (One-to-Many)
