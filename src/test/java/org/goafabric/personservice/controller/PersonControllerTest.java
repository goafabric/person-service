package org.goafabric.personservice.controller;

import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.logic.PersonLogic;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PersonControllerTest {
    private PersonLogic personLogic = Mockito.mock(PersonLogic.class);
    private PersonController personController = new PersonController(personLogic);

    @Test
    void getById() {
        when(personLogic.getById("0")).thenReturn(createPerson());
        assertThat(personController.getById("0").lastName()).isEqualTo("Simpson");
    }


    /*
    @Test
    void findByFirstName() {
        when(personLogic.findByFirstName("Homer",0,1)).thenReturn(Collections.singletonList(createPerson()));
        assertThat(personController.findByFirstName("Homer",0, 1)).isNotNull().isNotEmpty();
        assertThat(personController.findByFirstName("Homer",0, 1).getFirst().firstName()).isEqualTo("Homer");
    }

     */

    @Test
    void save() {
        assertThat(personController.save(createPerson())).isNull();
        verify(personLogic, times(1)).save(createPerson());
    }

    @Test
    void sayMyName() {
        assertThat(personController.sayMyName("Heisenberg")).isNull();
    }

    private static Person createPerson() {
        return new Person("0", null, "Homer", "Simpson", null);
    }

}