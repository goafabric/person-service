package org.goafabric.personservice.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.simpleNameStartingWith;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static org.goafabric.personservice.architecture.PersistenceRulesTest.BASE_PACKAGE;

@AnalyzeClasses(packages = BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
public class PersistenceRulesTest {
    static final String BASE_PACKAGE = "org.goafabric";

    @ArchTest
    static final ArchRule layerAreRespectedWithPersistence = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .ignoreDependency(simpleNameStartingWith("DemoDataImporter"), DescribedPredicate.alwaysTrue())

            .layer("Controller").definedBy("..controller")
            .layer("Logic").definedBy("..logic..")
            .layer("Persistence").definedBy("..persistence..")

            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Logic").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Logic")
            .allowEmptyShould(true);
}
