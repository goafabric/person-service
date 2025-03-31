package org.goafabric.personservice.controller

import jakarta.validation.ConstraintViolationException
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.adapter.Callee
import org.goafabric.personservice.adapter.CalleeServiceAdapter
import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.persistence.PersonRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInAotMode
internal class PersonControllerIT(
    @Autowired private val personController: PersonController,
    @Autowired private val personRepository: PersonRepository) {
    @MockitoBean  private val calleeServiceAdapter: CalleeServiceAdapter? = null

    @Test
    fun findById() {
        val persons: List<Person> = personController.findAll(0, 3)
        assertThat(persons).isNotNull().hasSize(3)

        val person
                : Person = personController.getById(persons.first().id!!)
        assertThat(person).isNotNull()
        assertThat(person.firstName).isEqualTo(persons.first().firstName)
        assertThat(person.lastName).isEqualTo(persons.first().lastName)

        assertThat(person.id).isNotNull()
        assertThat(person.version).isNotNull()

        assertThat(personRepository.findById(persons.first().id!!).get().organizationId).isEqualTo("0")
    }

    @Test
    fun findAll() {
        assertThat(personController.findAll(0, 3)).isNotNull().hasSize(3)
    }

    @Test
    fun findByFirstName() {
        val persons: List<Person> = personController.findByFirstName("Monty",0, 3)
        assertThat(persons).isNotNull().hasSize(1)
        assertThat(persons.first().firstName).isEqualTo("Monty")
        assertThat(persons.first().lastName).isEqualTo("Burns")
        assertThat(persons.first().address).isNotEmpty()
    }

    @Test
    fun findByAddressCity() {
        val persons: List<Person> = personController.findByStreet("Evergreen Terrace", 0, 3)
        assertThat(persons).isNotNull().isNotEmpty()
        assertThat(persons.first().address.first()!!.street).startsWith("Evergreen Terrace No.")
    }
    

    @Test
    fun save() {
        val person: Person? = personController.save(
            Person(
                null,
                null,
                "Homer",
                "Simpson",
                java.util.List.of(
                    createAddress("Evergreen Terrace"),
                    createAddress("Everblue Terrace")
                )
            )
        )

        assertThat(person).isNotNull()

        val person2: Person = personController.getById(person!!.id!!)
        assertThat(person2).isNotNull()
        assertThat(person2.address).hasSize(2)

        //update
        assertThat(
            personController.save(
                Person(person.id, person.version, person.firstName, person.lastName, person.address)
            )!!.id
        ).isEqualTo(person.id!!)

        personRepository.deleteById(person.id!!)
    }

    /*
    @Test
    fun saveWithValidationException() {
        Assertions.assertThatThrownBy {
            personController.save(
                Person(
                    null,
                    null,
                    "Homer",
                    "",
                    java.util.List.of(
                        createAddress("Evergreen Terrace"),
                        createAddress("Everblue Terrace")
                    )
                )
            )
        }.isInstanceOf(ConstraintViolationException::class.java)
    }

     */

    @Test
    fun sayMyName() {
        whenever(calleeServiceAdapter!!.sayMyName("Heisenberg")).thenReturn(Callee("", "Heisenberg"))
        assertThat(personController.sayMyName("Heisenberg")).isNotNull()
    }

    private fun createAddress(street: String): Address {
        return Address(
            null, null,
            street, "Springfield"
        )
    }
}
