package org.goafabric.personservice.controller;

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
    public void findById() {
        List<Person> persons = personController.findAll();
        assertThat(persons).isNotNull().hasSize(3);

        final Person person
                = personController.getById(persons.getFirst().id());
        assertThat(person).isNotNull();
        assertThat(person.firstName()).isEqualTo(persons.getFirst().firstName());
        assertThat(person.lastName()).isEqualTo(persons.getFirst().lastName());

        assertThat(personRepository.findById(persons.getFirst().id()).get().getOrganizationId()).isEqualTo("0");
    }

    @Test
    public void findAll() {
        assertThat(personController.findAll()).isNotNull().hasSize(3);

        assertThat(personController.findAll()).isNotNull().hasSize(3);
    }

    @Test
    public void findByFirstName() {
        List<Person> persons = personController.findByFirstName("Monty");
        assertThat(persons).isNotNull().hasSize(1);
        assertThat(persons.getFirst().firstName()).isEqualTo("Monty");
        assertThat(persons.getFirst().lastName()).isEqualTo("Burns");
        assertThat(persons.getFirst().address()).isNotEmpty();
    }

    @Test
    public void findByLastName() {
        List<Person> persons = personController.findByLastName("Simpson");
        assertThat(persons).isNotNull().hasSize(2);
        assertThat(persons.getFirst().lastName()).isEqualTo("Simpson");
        assertThat(persons.getFirst().address()).isNotEmpty();
    }

    @Test
    public void findByAddressCity() {
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

        //update
        assertThat(personController.save(
                new Person(person.id(), person.version(), person.firstName(), person.lastName(), person.address())).id()).isEqualTo(person.id());


        personRepository.deleteById(person.id());
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