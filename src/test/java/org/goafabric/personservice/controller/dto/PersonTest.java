package org.goafabric.personservice.controller.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidPerson() {
        Person person = new Person(null, null, "John", "Doe", List.of());
        var violations = validator.validate(person);
        assertThat(violations).isEmpty();
    }

    @Test
    void testNullFirstName() {
        Person person = new Person(null, null, null, "Doe", List.of());
        assertThat(validator.validate(person)).hasSize(1);
    }

    @Test
    void testShortFirstName() {
        Person person = new Person(null, null, "Jo", "Doe", List.of());
        assertThat(validator.validate(person)).hasSize(1);
    }

    @Test
    void testNullLastName() {
        Person person = new Person(null, null, "John", null, List.of());
        assertThat(validator.validate(person)).hasSize(1);
    }

    @Test
    void testShortLastName() {
        Person person = new Person(null, null, "John", "Do", List.of());
        assertThat(validator.validate(person)).hasSize(1);
    }
}
