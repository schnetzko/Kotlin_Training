package com.medical.management

import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractIntegrationTest {

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    // Repositories for cleanup
    @Autowired protected lateinit var treatmentRepository: TreatmentRepository
    @Autowired protected lateinit var healthDataRepository: HealthDataRepository
    @Autowired protected lateinit var examinationRepository: ExaminationRepository
    @Autowired protected lateinit var diagnosisRepository: DiagnosisRepository
    @Autowired protected lateinit var healthInsuranceRepository: HealthInsuranceRepository
    @Autowired protected lateinit var drugTherapyRepository: DrugTherapyRepository
    @Autowired protected lateinit var patientRepository: PatientRepository
    @Autowired protected lateinit var contactRepository: ContactRepository

    @AfterEach
    fun cleanupDatabase() {
        // Delete in dependency order (children before parents)
        treatmentRepository.deleteAll()
        healthDataRepository.deleteAll()
        examinationRepository.deleteAll()
        diagnosisRepository.deleteAll()
        healthInsuranceRepository.deleteAll()
        drugTherapyRepository.deleteAll()
        patientRepository.deleteAll()
        contactRepository.deleteAll()
    }

    companion object {
        @Container
        @JvmStatic
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15")
            .withDatabaseName("medical_data_test")
            .withUsername("test")
            .withPassword("test")

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
        }
    }
}
