package org.goafabric.personservice.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.goafabric.personservice.Application;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packagesOf = Application.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class LibraryTest {
    @ArchTest
    static final ArchRule libraries =
            noClasses()
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("com.google.common..")
                    .orShould()
                    .dependOnClassesThat()
                    .resideInAPackage("org.apache.commons..");
}
