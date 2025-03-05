package org.goafabric.personservice.v1.controller.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidPerson() {
        Person person = new Person(null, null, "John", "Doe", "middle", List.of());
        var violations = validator.validate(person);
        assertThat(violations).isEmpty();
    }

    @Test
    public void testNullFirstName() {
        Person person = new Person(null, null, null, "Doe", "middle", List.of());
        assertThat(validator.validate(person)).hasSize(1);
    }

    @Test
    public void testShortFirstName() {
        Person person = new Person(null, null, "Jo", "Doe", "middle", List.of());
        assertThat(validator.validate(person)).hasSize(1);
    }

    @Test
    public void testNullLastName() {
        Person person = new Person(null, null, "John", null, "middle", List.of());
        assertThat(validator.validate(person)).hasSize(1);
    }

    @Test
    public void testShortLastName() {
        Person person = new Person(null, null, "John", "Do", "middle", List.of());
        assertThat(validator.validate(person)).hasSize(1);
    }
}
