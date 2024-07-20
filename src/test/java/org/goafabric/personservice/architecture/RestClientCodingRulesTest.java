package org.goafabric.personservice.architecture;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.goafabric.personservice.Application;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.annotation.HttpExchange;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = Application.class, importOptions = DoNotIncludeTests.class)
class RestClientCodingRulesTest {

    @ArchTest
    static final ArchRule declarative_client_should_only_be_used =
        noClasses().that()
            .areNotAnnotatedWith(Configuration.class)
            .should()
            .dependOnClassesThat()
            .haveFullyQualifiedName("org.springframework.web.client.RestClient")
            .orShould().dependOnClassesThat()
            .haveFullyQualifiedName("org.springframework.web.client.RestTemplate")
            .orShould().dependOnClassesThat()
            .haveFullyQualifiedName("org.springframework.web.reactive.function.client.WebClient")
            .as("Only use the declarative REST Client, as otherwise native image support is broken");

    @ArchTest
    static final ArchRule declarative_client_should_use_circuit_breaker =
        methods().that()
            .areMetaAnnotatedWith(HttpExchange.class)
            .should()
            .beDeclaredInClassesThat()
            .areMetaAnnotatedWith(CircuitBreaker.class);
}