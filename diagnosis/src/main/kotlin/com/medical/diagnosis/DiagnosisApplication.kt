package com.medical.diagnosis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DiagnosisApplication

fun main(args: Array<String>) {
    runApplication<DiagnosisApplication>(*args)
}
