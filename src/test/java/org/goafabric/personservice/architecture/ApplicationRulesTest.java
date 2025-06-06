package org.goafabric.personservice.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.goafabric.personservice.Application;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = Application.class, importOptions = {ImportOption.DoNotIncludeTests.class, ApplicationRulesTest.IgnoreCglib.class})
public class ApplicationRulesTest {

    static class IgnoreCglib implements ImportOption {
        @Override
        public boolean includes(Location location) {
            return !location.contains("$$") && !location.contains("EnhancerByCGLIB");
        }
    }

    @ArchTest
    static final ArchRule reflectionShouldBeAvoided =
            noClasses()
                    .that()
                    .areNotAnnotatedWith(Configuration.class)
                    .and()
                    .doNotImplement(RuntimeHintsRegistrar.class)
                    .and()
                    .haveSimpleNameNotContaining("AuditTrailListener")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("java.lang.reflect")
                    .orShould()
                    .callMethod(Class.class, "forName", String.class)
                    .orShould()
                    .dependOnClassesThat()
                    .haveFullyQualifiedName("org.springframework.util.ReflectionUtils")
                    .orShould()
                    .dependOnClassesThat()
                    .haveFullyQualifiedName("org.springframework.beans.BeanWrapperImpl")
                    .orShould()
                    .dependOnClassesThat()
                    .haveFullyQualifiedName("jakarta.validation.ConstraintValidator")
                    .orShould()
                    .dependOnClassesThat()
                    .areAnnotatedWith(ConditionalOnProperty.class)
                    .because("Reflection breaks native image support");

    @ArchTest
    static final ArchRule aspectsNeedRuntimeHint =
            classes()
                    .that()
                    .areAnnotatedWith("org.aspectj.lang.annotation.Aspect").should()
                    .beAnnotatedWith(ImportRuntimeHints.class)
                    .allowEmptyShould(true)
                    .because("Aspects need a Reflection RuntimeHint with MemberCategory.INVOKE_DECLARED_METHODS");

    @ArchTest
    static final ArchRule librariesThatAreBanished =
            noClasses()
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("com.google.common..")
                    .orShould()
                    .dependOnClassesThat()
                    .resideInAPackage("dev.mccue.guava..")
                    .orShould()
                    .dependOnClassesThat()
                    .resideInAPackage("org.apache.commons..")
                    .because("Java 21+ and Spring cover the functionality already, managing extra libraries with transient dependencies should be avoided");@ArchTest

    static final ArchRule onlyAllowedLibraries = ArchRuleDefinition.classes()
            .should()
            .onlyDependOnClassesThat()
            .resideInAnyPackage(
                    "org.goafabric..",
                    "java..",
                    "javax..",
                    "jakarta..",
                    "org.springframework..",
                    "org.slf4j..",
                    "com.fasterxml.jackson..",
                    "org.flywaydb..",
                    "org.hibernate..",
                    "org.mapstruct..",
                    "io.github.resilience4j..",
                    "io.micrometer..",

                    "org.springdoc..",
                    "net.ttddyy..",

                    "org.javers.."
            )
            .because("Only core and allowed libraries should be used to avoid unnecessary third-party dependencies");


    @ArchTest
    static final ArchRule componentNamesThatAreBanished = noClasses()
            .that().haveSimpleNameNotContaining("Mapper")
            .should()
            .haveSimpleNameEndingWith("Impl")
            .andShould()
            .haveSimpleNameEndingWith("Management")
            .because("Avoid filler names like Impl or Management, use neutral Bean instead");

}
