package com.jacoco.aggregate.reports.app.entrypoint

import com.jacoco.aggregate.reports.app.utils.MessageUtils
import com.jacoco.aggregate.reports.utilities.StringUtils
import org.apache.commons.text.WordUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/jacoco_aggregate")
class WelcomeController {

    private val LOGGER = LoggerFactory.getLogger(WelcomeController::class.java)

    @GetMapping(value = ["/welcome"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun welcome(): ResponseEntity<String> {

        val tokens = StringUtils.split(MessageUtils.getMessage())
        val result = StringUtils.join(tokens)
        val response = WordUtils.capitalize(result)
        println(response)
        LOGGER.info("Responde {}", response)

        return ResponseEntity(response, HttpStatus.OK)
    }
}