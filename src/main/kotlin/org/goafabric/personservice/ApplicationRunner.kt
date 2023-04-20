package org.goafabric.personservice

import org.postgresql.util.PGobject
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints


@Configuration
@ImportRuntimeHints(ApplicationRunner.ApplicationRuntimeHints::class)
@RegisterReflectionForBinding(PGobject::class)
class ApplicationRunner {
    @Bean
    fun runner(context: ApplicationContext?): CommandLineRunner? {
        return CommandLineRunner { args: Array<String> ->
            if (args.isNotEmpty() && "-check-integrity" == args[0]) {
                SpringApplication.exit(context, ExitCodeGenerator { 0 })
            }
        }
    }

    internal class ApplicationRuntimeHints : RuntimeHintsRegistrar {
        override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
            hints.reflection().registerType(io.github.resilience4j.spring6.circuitbreaker.configure.CircuitBreakerAspect::class.java,  MemberCategory.INVOKE_DECLARED_METHODS)
        }
    }

}