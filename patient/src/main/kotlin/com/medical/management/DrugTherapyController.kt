package com.medical.management

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/drug-therapies")
class DrugTherapyController(
    private val repository: DrugTherapyRepository
) {
    private val logger = LoggerFactory.getLogger(DrugTherapyController::class.java)

    @GetMapping
    fun getAll(): List<DrugTherapy> {
        logger.info("GET /drug-therapies - fetching all drug therapies")
        val therapies = repository.findAll()
        logger.debug("GET /drug-therapies - returning {} drug therapy/therapies", therapies.size)
        return therapies
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @RequestBody drugTherapy: DrugTherapy
    ): DrugTherapy {
        logger.info("POST /drug-therapies - creating drug therapy: {}", drugTherapy)
        val saved = repository.save(drugTherapy)
        logger.debug("POST /drug-therapies - drug therapy created with id={}", saved.id)
        return saved
    }
}
