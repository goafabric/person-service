/*
package org.goafabric.personservice.logic;

import org.goafabric.personservice.crossfunctional.HttpInterceptor;
import org.goafabric.personservice.service.dto.Address;
import org.goafabric.personservice.service.dto.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonLogicIT {
    @Autowired
    private PersonLogic personLogic;

    @Test
    public void findById() {
        HttpInterceptor.setTenantId("0");
        List<Person> persons = personLogic.findAll();
        assertThat(persons).isNotNull().hasSize(3);

        final Person person
                = personLogic.getById(persons.get(0).getId());
        assertThat(person).isNotNull();
        assertThat(person.getFirstName()).isEqualTo(persons.get(0).getFirstName());
        assertThat(person.getLastName()).isEqualTo(persons.get(0).getLastName());

        HttpInterceptor.setTenantId("5a2f");
        //assertThatThrownBy(() ->  personLogic.getById(persons.get(0).getId()))
          //      .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void findAll() {
        HttpInterceptor.setTenantId("0");
        assertThat(personLogic.findAll()).isNotNull().hasSize(3);

        HttpInterceptor.setTenantId("5a2f");
        assertThat(personLogic.findAll()).isNotNull().hasSize(3);
    }

    @Test
    public void findByFirstName() {
        HttpInterceptor.setTenantId("0");
        List<Person> persons = personLogic.findByFirstName("Monty");
        assertThat(persons).isNotNull().hasSize(1);
        assertThat(persons.get(0).getFirstName()).isEqualTo("Monty");
        assertThat(persons.get(0).getLastName()).isEqualTo("Burns");

        HttpInterceptor.setTenantId("5a2f");
        assertThat(personLogic.findByFirstName("Monty")).isNotNull().hasSize(1);
    }

    @Test
    public void findByLastName() {
        HttpInterceptor.setTenantId("0");
        List<Person> persons = personLogic.findByLastName("Simpson");
        assertThat(persons).isNotNull().hasSize(2);
        assertThat(persons.get(0).getLastName()).isEqualTo("Simpson");

        HttpInterceptor.setTenantId("5a2f");
        assertThat(personLogic.findByLastName("Simpson")).isNotNull().hasSize(2);
    }
    @Test
    void save() {
        HttpInterceptor.setTenantId("4711");
        final Person person = personLogic.save(
            Person.builder().firstName("Homer").lastName("Simpson").address(Address.builder().build()).build()
        );
        assertThat(person).isNotNull();
    }
}

 */