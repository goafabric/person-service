package org.goafabric.personservice.controller;

import jakarta.validation.ConstraintViolationException;
import org.goafabric.personservice.adapter.Callee;
import org.goafabric.personservice.adapter.CalleeServiceAdapter;
import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.controller.dto.PersonSearch;
import org.goafabric.personservice.persistence.PersonRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInAotMode
class PersonControllerIT {

    @Autowired
    private PersonController personController;

    @Autowired
    private PersonRepository personRepository;

    @MockitoBean
    private CalleeServiceAdapter calleeServiceAdapter;

    @Test
    void getById() {
        List<Person> persons = personController.find(new PersonSearch(null, null), 0, 3);
        assertThat(persons).isNotNull().hasSize(3);

        final Person person
                = personController.getById(persons.getFirst().id());
        assertThat(person).isNotNull();
        assertThat(person.firstName()).isEqualTo(persons.getFirst().firstName());
        assertThat(person.lastName()).isEqualTo(persons.getFirst().lastName());

        assertThat(personRepository.findById(persons.getFirst().id()).get().getOrganizationId()).isEqualTo("0");
    }

    @Test
    void getByIdEntityNotFound() {
        assertThatThrownBy(() -> personController.getById("-1")).isInstanceOf(NoSuchElementException.class);
    }


    @Test
    void findByFirstName() {
        List<Person> persons = personController.find(new PersonSearch("Monty", null), 0 , 3);
        assertThat(persons).isNotNull().hasSize(1);
        assertThat(persons.getFirst().firstName()).isEqualTo("Monty");
        assertThat(persons.getFirst().lastName()).isEqualTo("Burns");
        assertThat(persons.getFirst().address()).isNotEmpty();
    }

    @Test
    void findByLastName() {
        List<Person> persons = personController.find(new PersonSearch(null, "Burns"), 0 , 3);
        assertThat(persons).isNotNull().hasSize(1);
        assertThat(persons.getFirst().firstName()).isEqualTo("Monty");
        assertThat(persons.getFirst().lastName()).isEqualTo("Burns");
        assertThat(persons.getFirst().address()).isNotEmpty();
    }

    @Test
    void findByAddressCity() {
        List<Person> persons = personController.findByStreet("Evergreen Terrace", 0, 3);
        assertThat(persons).isNotNull().isNotEmpty();
        assertThat(persons.getFirst().address().getFirst().street()).startsWith("Evergreen Terrace No.");
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
        var personUpdated = personController.save(new Person(person.id(), person.version(), person.firstName(), "updated", person.address()));
        assertThat(personUpdated.id()).isEqualTo(person.id());
        assertThat(personUpdated.version()).isEqualTo(1L);


        //optimistic locking
        var personLocked = new Person(person.id(), 0L, person.firstName(), "updated2", person.address());
        assertThatThrownBy(
                () -> personController.save(personLocked))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);

        personRepository.deleteById(person.id());
    }

    @Test
    void saveWithValidationException() {
        var person = new Person(null,
                null,
                "Homer",
                "",
                List.of(
                        createAddress("Evergreen Terrace"),
                        createAddress("Everblue Terrace")));

        assertThatThrownBy(() ->
            personController.save(person)
        ).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void sayMyName() {
        Mockito.when(calleeServiceAdapter.sayMyName("Heisenberg")).thenReturn(new Callee("", "Heisenberg"));
        assertThat(personController.sayMyName("Heisenberg")).isNotNull();
    }

    @Test
    void find() {
        var persons = personController.find(new PersonSearch("Homer", null), 0, 3);
        assertThat(persons).isNotNull().hasSize(1);
        System.out.println(persons);
    }

    private Address createAddress(String street) {
        return new Address(null, null,
                street, "Springfield");
    }

}