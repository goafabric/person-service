package org.goafabric.personservice

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import java.lang.module.ResolvedModule


@Configuration
@ImportRuntimeHints(ApplicationRunner.ApplicationRuntimeHints::class)
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
            //openapi
            hints.reflection().registerType(Module::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
            hints.reflection().registerType(ModuleLayer::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
            hints.reflection().registerType(java.lang.module.Configuration::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
            hints.reflection().registerType(ResolvedModule::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
        }
    }

}