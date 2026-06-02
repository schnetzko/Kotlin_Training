package com.medical.treatment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TreatmentApplication

fun main(args: Array<String>) {
    runApplication<TreatmentApplication>(*args)
}
