package com.medical.management

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDate

class DataLoaderIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `load sample data via all endpoints`() {
        // Test root endpoint
        val rootResponse: ResponseEntity<String> = restTemplate.getForEntity("/", String::class.java)
        assert(rootResponse.statusCode == HttpStatus.OK)
        assert(rootResponse.body == "Welcome to Medical Management API")
        println("Root endpoint: ${rootResponse.body}")

        // Create contacts
        val contact1 = Contact(
            firstName = "John",
            secondName = "Doe",
            mailingAddress = "123 Main St",
            zipCode = "12345",
            city = "Berlin",
            country = "Germany",
            phone = "123456789",
            email = "john.doe@example.com",
            type = "CONTACT"
        )
        val contact1Response: ResponseEntity<Contact> = restTemplate.postForEntity("/contacts", contact1, Contact::class.java)
        assert(contact1Response.statusCode == HttpStatus.CREATED)
        val savedContact1 = contact1Response.body!!
        println("Created contact: ${savedContact1.firstName} ${savedContact1.secondName}")

        val contact2 = Contact(
            firstName = "Jane",
            secondName = "Smith",
            mailingAddress = "456 Oak Ave",
            zipCode = "67890",
            city = "Munich",
            country = "Germany",
            phone = "987654321",
            email = "jane.smith@example.com",
            type = "CONTACT"
        )
        val contact2Response: ResponseEntity<Contact> = restTemplate.postForEntity("/contacts", contact2, Contact::class.java)
        assert(contact2Response.statusCode == HttpStatus.CREATED)
        println("Created contact: ${contact2Response.body?.firstName}")

        // Create specialists
        val specialist1 = Contact(
            firstName = "Dr. Hans",
            secondName = "Gruber",
            mailingAddress = "789 Doctor St",
            zipCode = "11111",
            city = "Vienna",
            country = "Austria",
            phone = "111222333",
            email = "dr.gruber@example.com",
            type = "SPECIALIST"
        )
        val specialist1Response: ResponseEntity<Contact> = restTemplate.postForEntity("/specialists", specialist1, Contact::class.java)
        assert(specialist1Response.statusCode == HttpStatus.CREATED)
        val savedSpecialist1 = specialist1Response.body!!
        println("Created specialist: ${savedSpecialist1.firstName}")

        val specialist2 = Contact(
            firstName = "Dr. Maria",
            secondName = "Meier",
            mailingAddress = "101 Clinic Rd",
            zipCode = "22222",
            city = "Frankfurt",
            country = "Germany",
            phone = "444555666",
            email = "dr.meier@example.com",
            type = "SPECIALIST"
        )
        val specialist2Response: ResponseEntity<Contact> = restTemplate.postForEntity("/specialists", specialist2, Contact::class.java)
        assert(specialist2Response.statusCode == HttpStatus.CREATED)
        val savedSpecialist2 = specialist2Response.body!!
        println("Created specialist: ${savedSpecialist2.firstName}")

        // Create patient
        val patient = Patient(
            first_name = "Max",
            second_name = "Mustermann",
            mailing_address = "Patient St 1",
            zip_code = "12345",
            city = "Berlin",
            country = "Germany",
            phone = "111111111",
            date_of_birth = LocalDate.of(1980, 5, 15)
        )
        val patientResponse: ResponseEntity<Patient> = restTemplate.postForEntity("/patients", patient, Patient::class.java)
        assert(patientResponse.statusCode == HttpStatus.CREATED)
        val savedPatient = patientResponse.body!!
        println("Created patient: ${savedPatient.first_name} ${savedPatient.second_name}")

        // Create health insurance
        val healthInsurance = HealthInsurance(
            name = "Statutory Health Insurance",
            insuranceNumber = "HI123456789",
            contact = savedContact1,
            coverage = Coverage.BY_LAW,
            patient = savedPatient
        )
        val healthInsuranceResponse: ResponseEntity<HealthInsurance> = restTemplate.postForEntity("/health-insurances", healthInsurance, HealthInsurance::class.java)
        assert(healthInsuranceResponse.statusCode == HttpStatus.CREATED)
        println("Created health insurance: ${healthInsuranceResponse.body?.name}")

        // Create drug therapy
        val drugTherapy = DrugTherapy(
            drugName = "Aspirin",
            description = "Pain relief medication",
            activeIngredient = "Acetylsalicylic acid",
            dosage = "1 tablet",
            frequence = "3 times daily"
        )
        val drugTherapyResponse: ResponseEntity<DrugTherapy> = restTemplate.postForEntity("/drug-therapies", drugTherapy, DrugTherapy::class.java)
        assert(drugTherapyResponse.statusCode == HttpStatus.CREATED)
        println("Created drug therapy: ${drugTherapyResponse.body?.drugName}")

        // Create diagnosis
        val diagnosis = Diagnosis(
            name = "Hypertension",
            code = "I10",
            specialist = savedSpecialist1.firstName,
            date = LocalDate.of(2024, 1, 15),
            type = DiagnosisType.CHRONIC_DISEASES,
            patient = savedPatient
        )
        val diagnosisResponse: ResponseEntity<Diagnosis> = restTemplate.postForEntity("/diagnoses", diagnosis, Diagnosis::class.java)
        assert(diagnosisResponse.statusCode == HttpStatus.CREATED)
        println("Created diagnosis: ${diagnosisResponse.body?.name}")

        // Create examination
        val examination = Examination(
            specialist = savedSpecialist2.firstName,
            name = "Blood Pressure Check",
            date = "2024-02-20",
            type = "Cardiology",
            patient = savedPatient
        )
        val examinationResponse: ResponseEntity<Examination> = restTemplate.postForEntity("/examinations", examination, Examination::class.java)
        assert(examinationResponse.statusCode == HttpStatus.CREATED)
        println("Created examination: ${examinationResponse.body?.name}")

        // Create health data
        val healthData = HealthData(
            patient = savedPatient,
            date = LocalDate.of(2024, 3, 1),
            size = 180.5,
            weight = 75.2,
            blood_count = "A+ve",
            blood_pressure = "120/80",
            urine_sample = "Normal",
            bloodType = "A+",
            allergies = "Penicillin"
        )
        val healthDataResponse: ResponseEntity<HealthData> = restTemplate.postForEntity("/health-data", healthData, HealthData::class.java)
        assert(healthDataResponse.statusCode == HttpStatus.CREATED)
        println("Created health data for patient: ${healthDataResponse.body?.patient?.first_name}")

        // Create treatment
        val treatment = Treatment(
            specialist = savedSpecialist1.firstName,
            name = "Medication Plan",
            date = "2024-03-15",
            type = "Chronic Care",
            patient = savedPatient
        )
        val treatmentResponse: ResponseEntity<Treatment> = restTemplate.postForEntity("/treatments", treatment, Treatment::class.java)
        assert(treatmentResponse.statusCode == HttpStatus.CREATED)
        println("Created treatment: ${treatmentResponse.body?.name}")

        // Verify all GET endpoints
        val contactsGet: ResponseEntity<Array<Contact>> = restTemplate.getForEntity("/contacts", Array<Contact>::class.java)
        assert(contactsGet.statusCode == HttpStatus.OK)
        assert(contactsGet.body?.isNotEmpty() == true)
        println("Retrieved ${contactsGet.body?.size} contacts")

        val specialistsGet: ResponseEntity<Array<Contact>> = restTemplate.getForEntity("/specialists", Array<Contact>::class.java)
        assert(specialistsGet.statusCode == HttpStatus.OK)
        assert(specialistsGet.body?.isNotEmpty() == true)
        println("Retrieved ${specialistsGet.body?.size} specialists")

        val patientsGet: ResponseEntity<Array<Patient>> = restTemplate.getForEntity("/patients", Array<Patient>::class.java)
        assert(patientsGet.statusCode == HttpStatus.OK)
        assert(patientsGet.body?.isNotEmpty() == true)
        println("Retrieved ${patientsGet.body?.size} patients")

        val healthInsurancesGet: ResponseEntity<Array<HealthInsurance>> = restTemplate.getForEntity("/health-insurances", Array<HealthInsurance>::class.java)
        assert(healthInsurancesGet.statusCode == HttpStatus.OK)
        println("Retrieved ${healthInsurancesGet.body?.size} health insurances")

        val drugTherapiesGet: ResponseEntity<Array<DrugTherapy>> = restTemplate.getForEntity("/drug-therapies", Array<DrugTherapy>::class.java)
        assert(drugTherapiesGet.statusCode == HttpStatus.OK)
        println("Retrieved ${drugTherapiesGet.body?.size} drug therapies")

        val diagnosesGet: ResponseEntity<Array<Diagnosis>> = restTemplate.getForEntity("/diagnoses", Array<Diagnosis>::class.java)
        assert(diagnosesGet.statusCode == HttpStatus.OK)
        println("Retrieved ${diagnosesGet.body?.size} diagnoses")

        val examinationsGet: ResponseEntity<Array<Examination>> = restTemplate.getForEntity("/examinations", Array<Examination>::class.java)
        assert(examinationsGet.statusCode == HttpStatus.OK)
        println("Retrieved ${examinationsGet.body?.size} examinations")

        val healthDataGet: ResponseEntity<Array<HealthData>> = restTemplate.getForEntity("/health-data", Array<HealthData>::class.java)
        assert(healthDataGet.statusCode == HttpStatus.OK)
        println("Retrieved ${healthDataGet.body?.size} health data entries")

        val treatmentsGet: ResponseEntity<Array<Treatment>> = restTemplate.getForEntity("/treatments", Array<Treatment>::class.java)
        assert(treatmentsGet.statusCode == HttpStatus.OK)
        println("Retrieved ${treatmentsGet.body?.size} treatments")

        println("\n=== All sample data loaded successfully! ===")
    }
}
