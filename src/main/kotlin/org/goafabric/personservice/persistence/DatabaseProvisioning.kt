package org.goafabric.personservice.persistence

import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.slf4j.LoggerFactory
import org.goafabric.personservice.crossfunctional.HttpInterceptor
import org.goafabric.personservice.logic.PersonLogic
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class DatabaseProvisioning(
    val personLogic: PersonLogic,
    val applicationContext: ApplicationContext) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${database.provisioning.goals:}") var goals: String? = null

    fun run() {
        if (goals!!.contains("-import-demo-data")) {
            log.info("Importing demo data ...")
            importDemoData()
        }
        if (goals!!.contains("-terminate")) {
            log.info("Terminating app ...")
            SpringApplication.exit(
                applicationContext,
                ExitCodeGenerator { 0 }) //if an exception is raised, spring will automatically terminate with 1
        }
    }

    private fun importDemoData() {
        HttpInterceptor.setTenantId("0")
        if (personLogic.findAll().isEmpty()) {
            HttpInterceptor.setTenantId("0")
            insertData()
            HttpInterceptor.setTenantId("5a2f")
            insertData()
        }
    }

    private fun insertData() {
        personLogic.save(
            Person(
                firstName = "Homer", lastName = "Simpson",
                address = (createAddress("Evergreen Terrace 1"))
        ))

        personLogic.save(
            Person(
                firstName = "Bart", lastName = "Simpson",
                address = (createAddress("Evergblue Terrace 1"))
        ))

        personLogic.save(
        Person(
            firstName = "Monty", lastName = "Burns",
            address = (createAddress("Burns Mansion"))
        )
        )
        
    }

    private fun createAddress(street: String): Address {
        return Address(street = street, city = "Springfield " + HttpInterceptor.getTenantId())
    }
    
}