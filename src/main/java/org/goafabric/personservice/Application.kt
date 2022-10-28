package org.goafabric.personservice

import org.goafabric.personservice.persistence.DatabaseProvisioning
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean

/**
 * Created by amautsch on 26.06.2015.
 */
@SpringBootApplication
class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(Application::class.java, *args)
        }
    }
    
    @Bean
    fun init(context: ApplicationContext?, databaseProvisioning: DatabaseProvisioning): CommandLineRunner {
        return CommandLineRunner { args: Array<String> ->
            databaseProvisioning.run()
            if (args.size > 0 && "-check-integrity" == args[0]) {
                SpringApplication.exit(context, ExitCodeGenerator { 0 })
            }
        }
    }

}