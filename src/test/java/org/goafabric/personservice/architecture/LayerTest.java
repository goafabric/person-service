package org.goafabric.personservice.architecture;


import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.goafabric.personservice.Application;
import org.goafabric.personservice.persistence.extensions.DemoDataImporter;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.equivalentTo;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packagesOf = Application.class, importOptions = DoNotIncludeTests.class)
class LayerTest {

    @ArchTest
    static final ArchRule layerAreRespected = layeredArchitecture()
        .consideringAllDependencies()
        .ignoreDependency(equivalentTo(DemoDataImporter.class), DescribedPredicate.alwaysTrue())

        .layer("Controller").definedBy("..controller")
        .layer("Logic").definedBy("..logic..")
        .layer("Persistence").definedBy("..persistence..")

        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
        .whereLayer("Logic").mayOnlyBeAccessedByLayers("Controller")
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Logic");

    @ArchTest
    static final ArchRule controllerNaming = classes()
            .that().areAnnotatedWith(RestController.class)
            .should().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule records = classes()
            .that().resideInAPackage("..dto..")
            .should().beRecords()
            .because("Classes in the dto package should be records");
}
