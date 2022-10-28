package org.goafabric.personservice.persistence

import org.slf4j.LoggerFactory
import org.goafabric.personservice.crossfunctional.HttpInterceptor
import org.goafabric.personservice.persistence.domain.AddressBo
import org.goafabric.personservice.persistence.domain.PersonBo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class DatabaseProvisioning(
        val personRepository: PersonRepository,
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
        if (personRepository.findAll().isEmpty()) {
            HttpInterceptor.setTenantId("0")
            insertData()
            HttpInterceptor.setTenantId("5a2f")
            insertData()
        }
    }

    private fun insertData() {
        personRepository.save(
            PersonBo(
                id = null,
                firstName = "Homer", lastName = "Simpson",
                address = (createAddress("Evergreen Terrace 1"))
        ))

        personRepository.save(
            PersonBo(
                id = null,
                firstName = "Bart", lastName = "Simpson",
                address = (createAddress("Evergblue Terrace 1"))
        ))

        personRepository.save(
        PersonBo(
            id = null,
            firstName = "Monty", lastName = "Burns",
            address = (createAddress("Burns Mansion"))
        ))
        
    }

    private fun createAddress(street: String): AddressBo {
        return AddressBo(street = street, city = "Springfield " + HttpInterceptor.getTenantId())
    }
    
}