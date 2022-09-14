/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.jacoco.aggregate.reports.app

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@EnableAutoConfiguration(exclude=[ DataSourceAutoConfiguration::class])
@ComponentScan(basePackages = arrayOf("com.jacoco.aggregate.reports.app", "com.jacoco.aggregate.reports.list", "com.jacoco.aggregate.reports.utilities"))
@OpenAPIDefinition(
    info = Info(
        title = "Jacoco Aggregate Reports Api",
        version = "0.1",
        description = "Jacoco Aggregate Reports Api",
        license = License(name = "Undertow", url = "http://www.emerzoom.com"),
        contact = Contact(url = "emerzoom@emerzoom.com", name = "Emerzoom", email = "emerzoom@emerzoom.com")
    )
)
open class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}