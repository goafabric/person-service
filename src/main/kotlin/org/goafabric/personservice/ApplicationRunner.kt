package org.goafabric.personservice

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationPredicate
import org.postgresql.util.PGobject
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.http.server.observation.ServerRequestObservationContext
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.web.SecurityFilterChain


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

    @Bean
    @Throws(Exception::class)
    fun filterChain(
        http: HttpSecurity,
        @Value("\${security.authentication.enabled:true}") isAuthenticationEnabled: Boolean
    ): SecurityFilterChain? {
        return if (isAuthenticationEnabled) http.authorizeHttpRequests { auth ->
            auth.requestMatchers("/actuator/**").permitAll().anyRequest().authenticated()
        }
            .httpBasic { }
            .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
            .build()
        else http.authorizeHttpRequests { auth ->
            auth.anyRequest().permitAll()
        }.build()
    }


    @Bean
    fun disableHttpServerObservationsFromName(): ObservationPredicate {
        return ObservationPredicate { name: String, context: Observation.Context? ->
            !(name.startsWith("spring.security.") || (context is ServerRequestObservationContext && context.carrier.requestURI.startsWith("/actuator")))
        }
    }

    internal class ApplicationRuntimeHints : RuntimeHintsRegistrar {
        override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
            hints.reflection().registerType(io.github.resilience4j.spring6.circuitbreaker.configure.CircuitBreakerAspect::class.java,  MemberCategory.INVOKE_DECLARED_METHODS)
        }
    }

}