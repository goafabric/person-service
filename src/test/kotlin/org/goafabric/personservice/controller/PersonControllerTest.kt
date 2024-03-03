package org.goafabric.personservice.controller

import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.logic.PersonLogic
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

internal class PersonControllerTest {
    private val personLogic: PersonLogic = Mockito.mock<PersonLogic>(PersonLogic::class.java)
    private val personController = PersonController(personLogic)
    
    @Test
    fun getById() {
        Mockito.`when`(personLogic.getById("0")).thenReturn(createPerson())
        assertThat(personController.getById("0").lastName).isEqualTo("Simpson")
    }

    @Test
    fun findAll() {
        Mockito.`when`(personLogic.findAll()).thenReturn(listOf(createPerson()))
        assertThat(personController.findAll()).isNotNull().isNotEmpty
        assertThat(personController.findAll()[0].lastName).isEqualTo("Simpson")
    }

    @Test
    fun findByFirstName() {
        Mockito.`when`(personLogic.findByFirstName("Homer")).thenReturn(listOf(createPerson()))
        assertThat(personController.findByFirstName("Homer")).isNotNull().isNotEmpty
        assertThat(personController.findByFirstName("Homer")[0].firstName).isEqualTo("Homer")
    }

    @Test
    fun findByLastName() {
        Mockito.`when`(personLogic.findByLastName("Simpson")).thenReturn(listOf(createPerson()))
        assertThat(personController.findByLastName("Simpson")).isNotNull().isNotEmpty
        assertThat(personController.findByLastName("Simpson")[0].lastName).isEqualTo("Simpson")
    }

    @Test
    fun save() {
        assertThat(personController.save(createPerson())).isNull()
        Mockito.verify(personLogic, Mockito.times(1)).save(createPerson())
    }

    @Test
    fun sayMyName() {
        assertThat(personController.sayMyName("Heisenberg")).isNull()
    }

    companion object {
        private fun createPerson(): Person {
            return Person("0", null, "Homer", "Simpson"
                , address = listOf(Address("", "", "", ""))
            )
        }
    }
}