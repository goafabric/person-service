package org.goafabric.personservice.controller;

import org.goafabric.personservice.v1.controller.PersonController;
import org.goafabric.personservice.v1.controller.dto.Person;
import org.goafabric.personservice.v1.logic.PersonLogic;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;

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


    @Test
    void findAll() {
        when(personLogic.findAll()).thenReturn(Collections.singletonList(createPerson()));
        assertThat(personController.findAll()).isNotNull().isNotEmpty();
        assertThat(personController.findAll().getFirst().lastName()).isEqualTo("Simpson");
    }

    @Test
    void findByFirstName() {
        when(personLogic.findByFirstName("Homer")).thenReturn(Collections.singletonList(createPerson()));
        assertThat(personController.findByFirstName("Homer")).isNotNull().isNotEmpty();
        assertThat(personController.findByFirstName("Homer").getFirst().firstName()).isEqualTo("Homer");
    }

    @Test
    void findByLastName() {
        when(personLogic.findByLastName("Simpson")).thenReturn(Collections.singletonList(createPerson()));
        assertThat(personController.findByLastName("Simpson")).isNotNull().isNotEmpty();
        assertThat(personController.findByLastName("Simpson").getFirst().lastName()).isEqualTo("Simpson");
    }

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