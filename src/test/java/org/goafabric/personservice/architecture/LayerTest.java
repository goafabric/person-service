package org.goafabric.personservice.architecture;


import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import org.goafabric.personservice.Application;

@AnalyzeClasses(packagesOf = Application.class, importOptions = DoNotIncludeTests.class)
public class LayerTest {

    /*
    @ArchTest
    static final ArchRule layers_are_respected = layeredArchitecture()
        .consideringAllDependencies()
        .ignoreDependency(equivalentTo(DemoDataImporter.class), DescribedPredicate.alwaysTrue())

        .layer("Controller").definedBy("..controller..")
        .layer("Logic").definedBy("..logic..")
        .layer("Persistence").definedBy("..persistence..")

        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
        .whereLayer("Logic").mayOnlyBeAccessedByLayers("Controller")
        .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Logic");

     */
}
