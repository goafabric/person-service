package org.goafabric.personservice.persistence.extensions

import org.goafabric.personservice.extensions.TenantContext
import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.controller.dto.PersonSearch
import org.goafabric.personservice.logic.PersonLogic
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Consumer
import java.util.stream.IntStream

@Component
class DemoDataImporter(
    @param:Value("\${database.provisioning.goals:}") private val goals: String, @param:Value(
        "\${multi-tenancy.tenants}"
    ) private val tenants: String,
    private val applicationContext: ApplicationContext
) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(this.javaClass)
    override fun run(vararg args: String) {
        if (args.size > 0 && "-check-integrity" == args[0]) {
            return
        }
        if (goals.contains("-import-demo-data")) {
            log.info("Importing demo data ...")
            importDemoData()
            log.info("Demo data import done ...")
        }
        if (goals.contains("-terminate")) {
            log.info("Terminating app ...")
            SpringApplication.exit(
                applicationContext,
                ExitCodeGenerator { 0 }) //if an exception is raised, spring will automatically terminate with 1
        }
    }

    private fun importDemoData() {
        Arrays.asList(*tenants.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()).forEach(
            Consumer { tenant: String? ->
                TenantContext.tenantId = tenant!!;
                if (applicationContext.getBean(PersonLogic::class.java).find(PersonSearch(),0, 1).isEmpty()) {
                    insertData()
                }
            })
        TenantContext.tenantId = "0";
    }

    private fun insertData() {
        IntStream.range(0, 1).forEach { i: Int ->
            applicationContext.getBean(PersonLogic::class.java).save(
                Person(
                    null, null, "Homer", "Simpson", listOf(createAddress("Evergreen Terrace No. $i"))
                )
            )
            applicationContext.getBean(PersonLogic::class.java).save(
                Person(
                    null, null, "Bart", "Simpson", listOf(createAddress("Everblue Terrace No. $i"))
                )
            )
            applicationContext.getBean(PersonLogic::class.java).save(
                Person(
                    null,
                    null,
                    "Monty",
                    "Burns",
                    listOf(createAddress("Mammon Street No. 1000 on the corner of Croesus"))
                )
            )
        }
    }

    private fun createAddress(street: String): Address {
        return Address(null, null, street, "Springfield " + TenantContext.tenantId)
    }
}
