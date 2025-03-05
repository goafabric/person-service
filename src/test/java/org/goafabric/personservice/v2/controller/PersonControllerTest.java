package org.goafabric.personservice.v2.controller;

import org.goafabric.personservice.v2.controller.dto.Person;
import org.goafabric.personservice.v2.logic.PersonLogic;
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
        assertThat(personController.getById("0").familyName()).isEqualTo("Simpson");
    }


    @Test
    void findAll() {
        when(personLogic.findAll()).thenReturn(Collections.singletonList(createPerson()));
        assertThat(personController.findAll()).isNotNull().isNotEmpty();
        assertThat(personController.findAll().getFirst().familyName()).isEqualTo("Simpson");
    }

    @Test
    void findByGivenName() {
        when(personLogic.findByGivenName("Homer")).thenReturn(Collections.singletonList(createPerson()));
        assertThat(personController.findByGivenName("Homer")).isNotNull().isNotEmpty();
        assertThat(personController.findByGivenName("Homer").getFirst().givenName()).isEqualTo("Homer");
    }

    @Test
    void findByLastName() {
        when(personLogic.findByFamilyName("Simpson")).thenReturn(Collections.singletonList(createPerson()));
        assertThat(personController.findByFamilyName("Simpson")).isNotNull().isNotEmpty();
        assertThat(personController.findByFamilyName("Simpson").getFirst().familyName()).isEqualTo("Simpson");
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
        return new Person("0", null, "Homer", "Simpson", "middle", null);
    }

}