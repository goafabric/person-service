package org.goafabric.personservice.controller

import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.logic.PersonLogic
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

internal class PersonControllerTest {
    private val personLogic: PersonLogic = mock<PersonLogic>(PersonLogic::class.java)
    private val personController = PersonController(personLogic)
    
    @Test
    fun getById() {
        `when`(personLogic.getById("0")).thenReturn(createPerson())
        assertThat(personController.getById("0").lastName).isEqualTo("Simpson")
    }

    @Test
    fun findAll() {
        `when`(personLogic.findAll()).thenReturn(listOf(createPerson()))
        assertThat(personController.findAll()).isNotNull().isNotEmpty
        assertThat(personController.findAll().first().lastName).isEqualTo("Simpson")
    }

    @Test
    fun findByFirstName() {
        `when`(personLogic.findByFirstName("Homer")).thenReturn(listOf(createPerson()))
        assertThat(personController.findByFirstName("Homer")).isNotNull().isNotEmpty
        assertThat(personController.findByFirstName("Homer").first().firstName).isEqualTo("Homer")
    }

    @Test
    fun findByLastName() {
        `when`(personLogic.findByLastName("Simpson")).thenReturn(listOf(createPerson()))
        assertThat(personController.findByLastName("Simpson")).isNotNull().isNotEmpty
        assertThat(personController.findByLastName("Simpson").first().lastName).isEqualTo("Simpson")
    }

    @Test
    fun save() {
        assertThat(personController.save(createPerson())).isNull()
        verify(personLogic, times(1)).save(createPerson())
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