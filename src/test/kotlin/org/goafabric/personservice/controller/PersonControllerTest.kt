package org.goafabric.personservice.controller

import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.controller.dto.PersonSearch
import org.goafabric.personservice.logic.PersonLogic
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

internal class PersonControllerTest {
    private val personLogic: PersonLogic = mock()
    private val personController = PersonController(personLogic)
    
    @Test
    fun getById() {
        whenever(personLogic.getById("0")).thenReturn(createPerson())
        assertThat(personController.getById("0").lastName).isEqualTo("Simpson")
    }

    @Test
    fun findByFirstName() {
        whenever(personLogic.find(PersonSearch(firstName = "Homer"), 0, 1)).thenReturn(listOf(createPerson()))
        assertThat(personController.find(PersonSearch(firstName = "Homer") ,0, 1)).isNotNull().isNotEmpty
        assertThat(personController.find(PersonSearch(firstName = "Homer") ,0, 1).first().firstName).isEqualTo("Homer")
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