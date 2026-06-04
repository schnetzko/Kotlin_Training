package com.medical.management

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class PatientApplication

fun main(args: Array<String>) {
    SpringApplication.run(PatientApplication::class.java, *args)
}
