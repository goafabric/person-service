//package org.goafabric.personservice.architecture;
//
//import com.tngtech.archunit.junit.ArchTest;
//import com.tngtech.archunit.lang.ArchRule;
//
//import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
//
//public class ReflectionTest {
//    @ArchTest
//    static final ArchRule declarative_client_should_only_be_used =
//            noMethods().that()
//                    .haveNameContaining("invoke")
//                    .or()
//                    .haveNameContaining("newinstance")
//                    .or()
//                    .haveNameContaining("getDeclaredMethod");
//}
