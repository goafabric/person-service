//package org.goafabric.personservice.architecture;
//
//import com.tngtech.archunit.base.DescribedPredicate;
//import com.tngtech.archunit.core.domain.JavaClass;
//import com.tngtech.archunit.core.domain.JavaMethod;
//import com.tngtech.archunit.core.importer.ImportOption;
//import com.tngtech.archunit.junit.AnalyzeClasses;
//import com.tngtech.archunit.junit.ArchTest;
//import com.tngtech.archunit.lang.ArchCondition;
//import com.tngtech.archunit.lang.ArchRule;
//import com.tngtech.archunit.lang.ConditionEvents;
//import com.tngtech.archunit.lang.SimpleConditionEvent;
//import jakarta.validation.Valid;
//import org.goafabric.personservice.Application;
//import org.springframework.validation.annotation.Validated;
//
//import java.util.Set;
//
//import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
//
//@AnalyzeClasses(packagesOf = Application.class, importOptions = ImportOption.DoNotIncludeTests.class)
//public class ValidationTest {
//
//    @ArchTest
//    static final ArchRule declarative_client_should_only_be_used =
//            classes()
//                .that(new DescribedPredicate<JavaClass>("have methods with @Valid parameter") {
//                    @Override
//                    public boolean test(JavaClass javaClass) {
//                        Set<JavaMethod> methods = javaClass.getMethods();
//                        boolean hasValidParameter = methods.stream()
//                                .flatMap(method -> method.getParameters().stream())
//                                .anyMatch(parameter -> parameter.isAnnotatedWith(Valid.class));
//
//                        if (hasValidParameter && !javaClass.isAnnotatedWith(Validated.class)) {
//                            return false;
//                            //String message = String.format("Class %s has methods with @Valid parameter but is not annotated with @Validated", javaClass.getName());
//                            //events.add(SimpleConditionEvent.violated(javaClass, message));
//                        }     {
//                            return true;
//                        }
//                    }
//                });
//
//}
//
