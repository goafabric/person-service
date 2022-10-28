package org.goafabric.personservice

import org.goafabric.personservice.persistence.DatabaseProvisioning
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ImportRuntimeHints
import java.lang.module.Configuration
import java.lang.module.ResolvedModule

/**
 * Created by amautsch on 26.06.2015.
 */
@SpringBootApplication
@ImportRuntimeHints(Application.ApplicationRuntimeHints::class)
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

    internal class ApplicationRuntimeHints : RuntimeHintsRegistrar {
        override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
            //openapi

            //openapi
            hints.reflection().registerType(Module::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
            hints.reflection().registerType(ModuleLayer::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
            hints.reflection().registerType(Configuration::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
            hints.reflection().registerType(ResolvedModule::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
        }
    }

}