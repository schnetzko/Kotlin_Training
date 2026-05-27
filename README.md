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
- Docker (required for integration tests — Testcontainers spins up a real PostgreSQL container automatically)

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

## Running Tests

The project distinguishes between **unit tests** and **integration tests**. Each can be executed independently, or both can be executed together via the `test` task.

### Integration Test Strategy

Integration tests (files matching `*IntegrationTest.kt`) use **[Testcontainers](https://testcontainers.com/)** to spin up a real PostgreSQL container for every test run. The lifecycle is fully automatic:

1. **Container start** — `@BeforeAll` starts a `postgres:16-alpine` container before any test in the class runs.
2. **Dynamic wiring** — `@DynamicPropertySource` overrides `spring.datasource.*` with the container's actual JDBC URL, username, and password, so no manual configuration is needed.
3. **Schema creation** — `spring.jpa.hibernate.ddl-auto: create-drop` (from `application-test.yml`) creates all tables at context startup and drops them on shutdown.
4. **Test execution** — each test method POSTs data through the full Spring MVC stack via `MockMvc` (dispatched in-process, not over a real TCP socket) and verifies the response as well as subsequent GET results against the live database.
5. **Container stop** — `@AfterAll` stops and removes the container after all tests in the class have finished.

> **`PatientIntegrationTest`** is the reference integration test. It POSTs a single patient via `POST /patients`, asserts the `201 Created` response body, then calls `GET /patients` and verifies the patient is persisted and returned correctly.

| Task | What it runs | Notes |
| --- | --- | --- |
| `./gradlew unitTest` | **Unit tests only** (`*Test.kt`, excluding `*IntegrationTest.kt`) | Fast, no Docker required. Prints a complete overview to stdout with total, passed, failed, skipped counts and total duration. |
| `./gradlew integrationTest` | **Integration tests only** (`*IntegrationTest.kt`) | Requires Docker (uses Testcontainers with PostgreSQL). Prints a complete overview to stdout. |
| `./gradlew test` | **All tests** — unit tests **and** integration tests combined | Standard Gradle test task. Requires Docker (because integration tests are included). |

### Run Only Unit Tests (separately)
Executes **only** the unit tests, excluding integration tests. Prints a complete overview to stdout including a final summary with total, passed, failed, skipped counts and total duration.
```bash
./gradlew unitTest
```

### Run Only Integration Tests (separately)
Executes **only** the integration tests (using Testcontainers). Prints a complete overview to stdout including a final summary with total, passed, failed, skipped counts and total duration. Requires a running Docker daemon.
```bash
./gradlew integrationTest
```

### Run All Tests (Unit + Integration)
Executes **both** unit tests and integration tests together. Requires a running Docker daemon (for the integration tests).
```bash
./gradlew test
```

### Run Tests with Build
Runs the full build, which includes `check` and therefore executes both unit and integration tests.
```bash
./gradlew build
```

### Run Specific Test Class
```bash
./gradlew test --tests "com.medical.management.PatientControllerTest"
```

### Skip Tests During Build
```bash
./gradlew build -x test -x integrationTest
```

## Dev Environment

### Debugging in VS Code

The project ships with pre-configured VS Code launch configurations ([`.vscode/launch.json`](.vscode/launch.json)) and tasks ([`.vscode/tasks.json`](.vscode/tasks.json)) so you can attach a debugger without any manual setup.

#### Option 1 — Compound: Boot Run + Attach Debugger (recommended)

This is the easiest way to debug. A single VS Code launch starts the application in debug mode **and** attaches the debugger automatically.

1. Open the **Run & Debug** panel (`Ctrl+Shift+D` / `⌘+Shift+D`).
2. Select **`Kotlin: Boot Run + Attach Debugger`** from the dropdown.
3. Click **▶ Start Debugging** (or press `F5`).

What happens under the hood:
- VS Code runs the `gradle: bootRunDebug` pre-launch task, which executes `./gradlew bootRunDebug`.
- The Gradle task ([`build.gradle.kts`](build.gradle.kts:179)) starts Spring Boot with the JDWP agent: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`.
- Once the app prints `Started … in … seconds`, VS Code attaches the Java debugger to `localhost:5005`.
- Set breakpoints anywhere in the source — they will be hit on the next matching request.

> **Note:** `suspend=n` means the application boots immediately without waiting for the debugger to connect, so the app is usable even if you haven't attached yet.

#### Option 2 — Attach manually to a running instance

If you already started the application with `./gradlew bootRunDebug` from a terminal:

1. Open the **Run & Debug** panel.
2. Select **`Kotlin: Attach to Spring Boot (5005)`**.
3. Click **▶ Start Debugging** (or press `F5`).

The debugger attaches to the JDWP agent already listening on port `5005`.

#### Option 3 — Java debugger launch (compile & run)

This configuration compiles the project first and then launches the application directly through the VS Code Java debugger (no separate terminal needed):

1. Open the **Run & Debug** panel.
2. Select **`Kotlin: Run DemoApplication (Java debugger)`**.
3. Click **▶ Start Debugging** (or press `F5`).

The `gradle: classes` pre-launch task compiles the Kotlin sources before the JVM is started. The active Spring profile is set to `default` via `-Dspring.profiles.active=default`.

> **Prerequisite:** An `.env` file must exist in the workspace root if environment variables are required (the launch config references `${workspaceFolder}/.env`).

#### Debug port reference

| Setting | Value |
| --- | --- |
| Protocol | JDWP over TCP/IP |
| Host | `localhost` |
| Port | `5005` |
| Suspend on start | `n` (app starts immediately) |

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
