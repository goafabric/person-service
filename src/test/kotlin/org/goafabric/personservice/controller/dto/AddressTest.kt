package org.goafabric.personservice.controller.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class AddressTest {
    @Test
    fun testValidAddress() {
        val address = Address(null, null, "123 Main St", "Springfield")
        val violations = validator!!.validate(address)
        Assertions.assertThat(violations).isEmpty()
    }

    @Test
    fun testShortStreet() {
        val address = Address(null, null, "St", "Springfield")
        val violations = validator!!.validate(address)
        Assertions.assertThat(violations).hasSize(1)
    }

    @Test
    fun testShortCity() {
        val address = Address(null, null, "123 Main St", "NY")
        val violations = validator!!.validate(address)
        Assertions.assertThat(violations).hasSize(1)
    }

    @Test
    fun testInvalidCity() {
        val address = Address(null, null, "@123 Main St", "Springfield")
        val violations = validator!!.validate(address)
        Assertions.assertThat(violations).hasSize(1)
    }

    companion object {
        private var validator: Validator? = null

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val factory = Validation.buildDefaultValidatorFactory()
            validator = factory.validator
        }
    }
}
