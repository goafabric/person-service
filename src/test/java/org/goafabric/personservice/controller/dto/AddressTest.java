package org.goafabric.personservice.controller.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.goafabric.personservice.base.controller.dto.Address;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidAddress() {
        Address address = new Address(null, null, "123 Main St", "Springfield");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations).isEmpty();
    }

    @Test
    public void testNullStreet() {
        Address address = new Address(null, null, null, "Springfield");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void testShortStreet() {
        Address address = new Address(null, null, "St", "Springfield");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void testNullCity() {
        Address address = new Address(null, null, "123 Main St", null);
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void testShortCity() {
        Address address = new Address(null, null, "123 Main St", "NY");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations).hasSize(1);
    }

    @Test
    public void testInvalidCity() {
        Address address = new Address(null, null, "@123 Main St", "Springfield");
        Set<ConstraintViolation<Address>> violations = validator.validate(address);
        assertThat(violations).hasSize(1);
    }

}
