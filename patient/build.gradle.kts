plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20"
    jacoco
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
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

jacoco {
    toolVersion = "0.8.11"
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
    // Collect JaCoCo execution data for every test task
    finalizedBy("jacocoTestReport")
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
    afterSuite(
        KotlinClosure2({
                desc: org.gradle.api.tasks.testing.TestDescriptor,
                result: org.gradle.api.tasks.testing.TestResult
            ->
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
                println("Duration:      $durationMs ms")
                println(separator)
            }
        })
    )
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
    afterSuite(
        KotlinClosure2({
                desc: org.gradle.api.tasks.testing.TestDescriptor,
                result: org.gradle.api.tasks.testing.TestResult
            ->
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
                println("Duration:      $durationMs ms")
                println(separator)
            }
        })
    )
}

// Starts the application with JDWP debug agent on port 5006 (suspend=n so the app boots immediately)
tasks.register<org.springframework.boot.gradle.tasks.run.BootRun>("bootRunDebug") {
    description = "Runs the Spring Boot application with a remote-debug agent on port 5006."
    group = "application"
    mainClass.set("com.medical.management.PatientApplicationKt")
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs(
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006"
    )
}

// ---------------------------------------------------------------------------
// JaCoCo coverage reports
// ---------------------------------------------------------------------------

// Default jacocoTestReport – covers the standard `test` task execution data
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/test/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
    }
    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("classes/kotlin/main")) {
            exclude(
                "**/DemoApplicationKt.class",
                "**/*\$*.class" // exclude Kotlin-generated lambdas / companion objects
            )
        }
    )
    sourceDirectories.setFrom(files("src/main/kotlin"))
    executionData.setFrom(fileTree(layout.buildDirectory).include("jacoco/test.exec"))
}

// Coverage report that merges unit-test execution data
tasks.register<JacocoReport>("jacocoUnitTestReport") {
    group = "verification"
    description = "Generates JaCoCo coverage report for unit tests."
    dependsOn("unitTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/unitTest/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/unitTest/jacocoUnitTestReport.xml"))
    }
    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("classes/kotlin/main")) {
            exclude(
                "**/DemoApplicationKt.class",
                "**/*\$*.class"
            )
        }
    )
    sourceDirectories.setFrom(files("src/main/kotlin"))
    executionData.setFrom(fileTree(layout.buildDirectory).include("jacoco/unitTest.exec"))
}

// Coverage report that merges integration-test execution data
tasks.register<JacocoReport>("jacocoIntegrationTestReport") {
    group = "verification"
    description = "Generates JaCoCo coverage report for integration tests."
    dependsOn("integrationTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/integrationTest/html"))
        xml.outputLocation.set(
            layout.buildDirectory.file("reports/jacoco/integrationTest/jacocoIntegrationTestReport.xml")
        )
    }
    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("classes/kotlin/main")) {
            exclude(
                "**/DemoApplicationKt.class",
                "**/*\$*.class"
            )
        }
    )
    sourceDirectories.setFrom(files("src/main/kotlin"))
    executionData.setFrom(fileTree(layout.buildDirectory).include("jacoco/integrationTest.exec"))
}

// Merged report – combines unit + integration execution data into one report
tasks.register<JacocoReport>("jacocoCombinedReport") {
    group = "verification"
    description = "Generates a merged JaCoCo coverage report from unit and integration tests."
    dependsOn("unitTest", "integrationTest")
    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/combined/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/combined/jacocoCombinedReport.xml"))
    }
    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("classes/kotlin/main")) {
            exclude(
                "**/DemoApplicationKt.class",
                "**/*\$*.class"
            )
        }
    )
    sourceDirectories.setFrom(files("src/main/kotlin"))
    executionData.setFrom(
        fileTree(layout.buildDirectory).include(
            "jacoco/unitTest.exec",
            "jacoco/integrationTest.exec"
        )
    )
}

// Make `check` depend on the integration tests so a standard verification run covers both
tasks.named("check") {
    dependsOn("integrationTest")
}

// ---------------------------------------------------------------------------
// ktlint – Kotlin linting & formatting
// ---------------------------------------------------------------------------

ktlint {
    version.set("1.3.1")
    android.set(false)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    filter {
        exclude("**/generated/**")
        exclude("**/test/**")
    }
}

// ktlintCheck runs as part of `check` (already wired by the plugin);
// make it explicit so the dependency is visible in the task graph.
tasks.named("check") {
    dependsOn("ktlintCheck")
}
