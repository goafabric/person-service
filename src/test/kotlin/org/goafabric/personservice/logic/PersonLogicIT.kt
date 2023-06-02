package org.goafabric.personservice.logic


import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.crossfunctional.HttpInterceptor
import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.persistence.PersonRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PersonLogicIT (
    @Autowired
    private val personLogic: PersonLogic,

    @Autowired
    private val personRepository: PersonRepository) {

    @Test
    fun findById() {
        val persons = personLogic.findAll()
        assertThat(persons).isNotNull.hasSize(3)
        val person = personLogic.getById(persons[0].id!!)
        assertThat(person).isNotNull
        assertThat(person.firstName).isEqualTo(persons[0].firstName)
        assertThat(person.lastName).isEqualTo(persons[0].lastName)
    }

    @Test
    fun findAll() {
        assertThat(personLogic.findAll()).isNotNull.hasSize(3)
    }

    @Test
    fun findByFirstName() {
        val persons = personLogic.findByFirstName("Monty")
        assertThat(persons).isNotNull.hasSize(1)
        assertThat(persons[0].firstName).isEqualTo("Monty")
        assertThat(persons[0].lastName).isEqualTo("Burns")
    }

    @Test
    fun findByLastName() {
        val persons = personLogic.findByLastName("Simpson")
        assertThat(persons).isNotNull.hasSize(2)
        assertThat(persons[0].lastName).isEqualTo("Simpson")
    }

    @Test
    fun save() {
        val person = personLogic.save(
            Person(firstName = "Homer", lastName = "Simpson",
                    address = Address(street = "Evergreeen ", city = "Springfield " + HttpInterceptor.getTenantId()))
        )

        assertThat(person).isNotNull
        personRepository.deleteById(person?.id ?: "")
    }
}
