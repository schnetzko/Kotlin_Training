plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.testcontainers:testcontainers:1.20.4")
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
    environment("DOCKER_HOST", "unix:///var/run/docker.sock")
    environment("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE", "/var/run/docker.sock")
    environment("DOCKER_API_VERSION", "1.44")
    jvmArgs(
        "-Dapi.version=1.44",
        "-Ddocker.host=unix:///var/run/docker.sock"
    )
}

tasks.register<Test>("unitTest") {
    description = "Runs only the unit tests (excludes integration tests) and prints a complete overview to stdout."
    group = "verification"

    useJUnitPlatform()

    // Include only unit tests; exclude integration tests
    include("**/*Test.class")
    exclude("**/*IntegrationTest.class")
    exclude("**/AbstractIntegrationTest.class")

    // Always run the task when invoked
    outputs.upToDateWhen { false }

    // Print detailed information for each test to stdout
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
        )
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    // Print a complete summary overview after all tests have executed
    afterSuite(KotlinClosure2({ desc: org.gradle.api.tasks.testing.TestDescriptor, result: org.gradle.api.tasks.testing.TestResult ->
        if (desc.parent == null) { // root suite only
            val total = result.testCount
            val passed = result.successfulTestCount
            val failed = result.failedTestCount
            val skipped = result.skippedTestCount
            val durationMs = result.endTime - result.startTime

            val separator = "=".repeat(80)
            println()
            println(separator)
            println("                          UNIT TEST OVERVIEW")
            println(separator)
            println("Result:        ${result.resultType}")
            println("Total tests:   $total")
            println("Passed:        $passed")
            println("Failed:        $failed")
            println("Skipped:       $skipped")
            println("Duration:      ${durationMs} ms")
            println(separator)
        }
    }))
}

tasks.register<Test>("integrationTest") {
    description = "Runs only the integration tests (uses Testcontainers) and prints a complete overview to stdout."
    group = "verification"

    useJUnitPlatform()

    // Include only integration tests
    include("**/*IntegrationTest.class")
    exclude("**/AbstractIntegrationTest.class")

    // Use a separate report/results directory to avoid clashes with `test` and `unitTest`
    reports {
        html.outputLocation.set(layout.buildDirectory.dir("reports/tests/integrationTest"))
        junitXml.outputLocation.set(layout.buildDirectory.dir("test-results/integrationTest"))
    }

    // Always run the task when invoked
    outputs.upToDateWhen { false }

    // Ensure Testcontainers / Docker environment variables are present (mirrors tasks.withType<Test>)
    environment("DOCKER_HOST", "unix:///var/run/docker.sock")
    environment("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE", "/var/run/docker.sock")
    environment("DOCKER_API_VERSION", "1.44")
    jvmArgs(
        "-Dapi.version=1.44",
        "-Ddocker.host=unix:///var/run/docker.sock"
    )

    // Print detailed information for each test to stdout
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.STARTED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_ERROR
        )
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }

    // Print a complete summary overview after all tests have executed
    afterSuite(KotlinClosure2({ desc: org.gradle.api.tasks.testing.TestDescriptor, result: org.gradle.api.tasks.testing.TestResult ->
        if (desc.parent == null) { // root suite only
            val total = result.testCount
            val passed = result.successfulTestCount
            val failed = result.failedTestCount
            val skipped = result.skippedTestCount
            val durationMs = result.endTime - result.startTime

            val separator = "=".repeat(80)
            println()
            println(separator)
            println("                       INTEGRATION TEST OVERVIEW")
            println(separator)
            println("Result:        ${result.resultType}")
            println("Total tests:   $total")
            println("Passed:        $passed")
            println("Failed:        $failed")
            println("Skipped:       $skipped")
            println("Duration:      ${durationMs} ms")
            println(separator)
        }
    }))
}

// Make `check` depend on the integration tests so a standard verification run covers both
tasks.named("check") {
    dependsOn("integrationTest")
}