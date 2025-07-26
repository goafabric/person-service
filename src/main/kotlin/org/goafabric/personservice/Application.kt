package org.goafabric.personservice

import org.hibernate.annotations.DialectOverride
import org.hibernate.boot.model.relational.ColumnOrderingStrategyStandard
import org.hibernate.boot.models.DialectOverrideAnnotations
import org.hibernate.boot.models.annotations.internal.CacheAnnotation
import org.hibernate.dialect.type.*
import org.hibernate.engine.internal.`VersionLogger_$logger`
import org.hibernate.internal.`SessionFactoryRegistryMessageLogger_$logger`
import org.hibernate.validator.internal.util.logging.`Log_$logger`
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.client.RestClient

@SpringBootApplication
@RegisterReflection(
    classes = [ColumnOrderingStrategyStandard::class, CacheAnnotation::class, DialectOverride::class, DialectOverrideAnnotations::class, `Log_$logger`::class, `VersionLogger_$logger`::class, `SessionFactoryRegistryMessageLogger_$logger`::class, PostgreSQLInetJdbcType::class, PostgreSQLIntervalSecondJdbcType::class, PostgreSQLStructPGObjectJdbcType::class, PostgreSQLJsonPGObjectJsonbType::class, PostgreSQLJsonArrayPGObjectJsonbJdbcTypeConstructor::class
    ],
    memberCategories = [MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.ACCESS_DECLARED_FIELDS]
)

class Application(@Autowired private val context: ConfigurableApplicationContext):
    CommandLineRunner {
    override fun run(vararg args: String?) {
        if (args.isNotEmpty() && "-check-integrity" == args[0]) {
            context.addApplicationListener(ApplicationListener { _: ApplicationReadyEvent? ->
                RestClient.create().get()
                    .uri("http://localhost:" + context.environment.getProperty("local.server.port") + "/v3/api-docs")
                    .retrieve().body(
                        String::class.java
                    )
                SpringApplication.exit(context, ExitCodeGenerator { 0 })
            })
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}