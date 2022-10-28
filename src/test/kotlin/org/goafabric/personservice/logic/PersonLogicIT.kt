package org.goafabric.personservice.logic


import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.crossfunctional.HttpInterceptor
import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class PersonLogicIT (
    @Autowired
    private val personLogic: PersonLogic) {

    @Test
    fun findById() {
        HttpInterceptor.setTenantId("0")
        val persons = personLogic.findAll()
        assertThat(persons).isNotNull.hasSize(3)
        val person = personLogic.getById(persons[0].id!!)
        assertThat(person).isNotNull
        assertThat(person.firstName).isEqualTo(persons[0].firstName)
        assertThat(person.lastName).isEqualTo(persons[0].lastName)
        HttpInterceptor.setTenantId("5a2f")
        //assertThatThrownBy(() ->  personLogic.getById(persons.get(0).getId()))
        //      .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    fun findAll() {
        HttpInterceptor.setTenantId("0")
        assertThat(personLogic!!.findAll()).isNotNull.hasSize(3)
        HttpInterceptor.setTenantId("5a2f")
        assertThat(personLogic.findAll()).isNotNull.hasSize(3)
    }

    @Test
    fun findByFirstName() {
        HttpInterceptor.setTenantId("0")
        val persons = personLogic.findByFirstName("Monty")
        assertThat(persons).isNotNull.hasSize(1)
        assertThat(persons[0].firstName).isEqualTo("Monty")
        assertThat(persons[0].lastName).isEqualTo("Burns")
        HttpInterceptor.setTenantId("5a2f")
        assertThat(personLogic.findByFirstName("Monty")).isNotNull.hasSize(1)
    }

    @Test
    fun findByLastName() {
        HttpInterceptor.setTenantId("0")
        val persons = personLogic.findByLastName("Simpson")
        assertThat(persons).isNotNull.hasSize(2)
        assertThat(persons[0].lastName).isEqualTo("Simpson")
        HttpInterceptor.setTenantId("5a2f")
        assertThat(personLogic.findByLastName("Simpson")).isNotNull.hasSize(2)
    }

    @Test
    fun save() {
        HttpInterceptor.setTenantId("4711")
        val person = personLogic.save(
            Person(firstName = "Homer", lastName = "Simpson",
                    address = Address(street = "Evergreeen ", city = "Springfield " + HttpInterceptor.getTenantId()))
        )

        assertThat(person).isNotNull
    }
}
