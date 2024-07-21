package org.goafabric.personservice.architecture;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.annotation.HttpExchange;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.goafabric.personservice.architecture.PersistenceRulesTest.BASE_PACKAGE;

@AnalyzeClasses(packages = BASE_PACKAGE, importOptions = DoNotIncludeTests.class)
class AdapterRulesTest {
    @ArchTest
    static final ArchRule adapterName =
            methods().that()
                    .areMetaAnnotatedWith(HttpExchange.class)
                    .should().beDeclaredInClassesThat().haveSimpleNameEndingWith("Adapter")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule declarativeClient =
        noClasses().that()
            .areNotAnnotatedWith(Configuration.class)
            .should()
            .dependOnClassesThat()
            .haveFullyQualifiedName("org.springframework.web.client.RestClient")
            .orShould().dependOnClassesThat()
            .haveFullyQualifiedName("org.springframework.web.client.RestTemplate")
            .orShould().dependOnClassesThat()
            .haveFullyQualifiedName("org.springframework.web.reactive.function.client.WebClient")
            .as("Only use the declarative REST Client, as otherwise native image support is broken")
            .allowEmptyShould(true);


    @ArchTest
    static final ArchRule declarativeClientShouldUserCircuitBreaker =
        methods().that()
            .areMetaAnnotatedWith(HttpExchange.class)
            .should()
            .beDeclaredInClassesThat()
            .areMetaAnnotatedWith("io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker")
            .allowEmptyShould(true);

}
