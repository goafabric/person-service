package org.goafabric.personservice.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import org.goafabric.personservice.Application;

@AnalyzeClasses(packagesOf = Application.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class NoLombokTest {
    /*
    @ArchTest
    static final ArchRule lombok =
            noClasses()
                    .should()
                    .dependOnClassesThat()
                    .haveFullyQualifiedName("lombok.Data")
                    .resideInAPackage("lombok..")

     */

}
