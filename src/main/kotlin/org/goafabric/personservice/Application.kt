package org.goafabric.personservice

import org.flywaydb.core.internal.publishing.PublishingConfigurationExtension
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
