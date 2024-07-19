package org.goafabric.personservice.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.goafabric.personservice.adapter.Callee;
import org.goafabric.personservice.adapter.CalleeServiceAdapter;
import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.persistence.PersonRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.aot.DisabledInAotMode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInAotMode
class PersonControllerIT {

    @Autowired
    private PersonController personController;

    @Autowired
    private PersonRepository personRepository;

    @MockBean
    private CalleeServiceAdapter calleeServiceAdapter;

    @Test
    void getById() {
        List<Person> persons = personController.findAll();
        assertThat(persons).isNotNull().hasSize(3);

        final Person person
                = personController.getById(persons.getFirst().id());
        assertThat(person).isNotNull();
        assertThat(person.firstName()).isEqualTo(persons.getFirst().firstName());
        assertThat(person.lastName()).isEqualTo(persons.getFirst().lastName());

        assertThat(personRepository.getById(persons.getFirst().id()).getOrganizationId()).isEqualTo("0");
    }

    @Test
    void getByIdEntityNotFound() {
        assertThatThrownBy(() -> personController.getById("-1")).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findAll() {
        assertThat(personController.findAll()).isNotNull().hasSize(3);

        assertThat(personController.findAll()).isNotNull().hasSize(3);
    }

    @Test
    void findByFirstName() {
        List<Person> persons = personController.findByFirstName("Monty");
        assertThat(persons).isNotNull().hasSize(1);
        assertThat(persons.getFirst().firstName()).isEqualTo("Monty");
        assertThat(persons.getFirst().lastName()).isEqualTo("Burns");
        assertThat(persons.getFirst().address()).isNotEmpty();
    }

    @Test
    void findByLastName() {
        List<Person> persons = personController.findByLastName("Simpson");
        assertThat(persons).isNotNull().hasSize(2);
        assertThat(persons.getFirst().lastName()).isEqualTo("Simpson");
        assertThat(persons.getFirst().address()).isNotEmpty();
    }

    @Test
    void findByAddressCity() {
        List<Person> persons = personController.findByStreet("Evergreen Terrace");
        assertThat(persons).isNotNull().isNotEmpty();
        assertThat(persons.getFirst().address().getFirst().street()).startsWith("Evergreen Terrace No.");
        //assertThat(persons.getFirst().lastName()).isEqualTo("Simpson");
    }

    @Test
    void save() {
        final Person person = personController.save(
                new Person(null,
                        null,
                        "Homer",
                        "Simpson",
                        List.of(
                                createAddress("Evergreen Terrace"),
                                createAddress("Everblue Terrace"))
                ));

        assertThat(person).isNotNull();

        var person2 = personController.getById(person.id());
        assertThat(person2).isNotNull();
        assertThat(person2.address()).hasSize(2);

        var person3 = new Person(person.id(), person.version(), person.firstName(), "updated", person.address());

        //update
        var personUpdated = personController.save(person3);
        assertThat(personUpdated.id()).isEqualTo(person.id());
        //assertThat(personUpdated.version()).isEqualTo(1L);
        
        personRepository.deleteById(person.id());
    }

    @Test
    void saveWithValidationException() {
        assertThatThrownBy(() ->
            personController.save(
                new Person(null,
                        null,
                        "Homer",
                        "",
                        List.of(
                                createAddress("Evergreen Terrace"),
                                createAddress("Everblue Terrace"))
                ))
        ).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void sayMyName() {
        Mockito.when(calleeServiceAdapter.sayMyName("Heisenberg")).thenReturn(new Callee("", "Heisenberg"));
        assertThat(personController.sayMyName("Heisenberg")).isNotNull();
    }

    private Address createAddress(String street) {
        return new Address(null, null,
                street, "Springfield");
    }

}