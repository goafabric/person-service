package org.goafabric.personservice.controller.dto

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class PersonTest {
    @Test
    fun testValidPerson() {
        val person = Person(null, null, "John", "Doe", listOf<Address>())
        val violations = validator!!.validate(person)
        Assertions.assertThat(violations).isEmpty()
    }

    @Test
    fun testShortFirstName() {
        val person = Person(null, null, "Jo", "Doe", listOf<Address>())
        Assertions.assertThat(validator!!.validate(person)).hasSize(1)
    }

    @Test
    fun testShortLastName() {
        val person = Person(null, null, "John", "Do", listOf<Address>())
        Assertions.assertThat(validator!!.validate(person)).hasSize(1)
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
