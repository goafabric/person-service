package org.goafabric.personservice.v1.logic;

import org.goafabric.personservice.v1.controller.dto.Person;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional
public class PersonProjection {
    private final PersonMapper personMapper;

    private final org.goafabric.personservice.v2.logic.PersonLogic personLogicV2;

    public PersonProjection(PersonMapper personMapper, org.goafabric.personservice.v2.logic.PersonLogic personLogicV2) {
        this.personMapper = personMapper;
        this.personLogicV2 = personLogicV2;
    }

    public Person getById(String id) {
        return personMapper.map(
                personLogicV2.getById(id));
    }

    public List<Person> findAll() {
        return personMapper.map(
                personLogicV2.findAll());
    }

    public List<Person> findByFirstName(String firstName) {
        return personMapper.map(
                personLogicV2.findByGivenName(firstName));
    }

    public List<Person> findByLastName(String lastName) {
        return personMapper.map(
                personLogicV2.findByFamilyName(lastName));
    }

    public List<Person> findByStreet(String street) {
        return personMapper.map(
                personLogicV2.findByStreet(street));
    }

    public Person save(Person person) {
        return personMapper.map(
                personLogicV2.save(
                        personMapper.map(person)));

    }

    public Person sayMyName(String name) {
        return personMapper.map(
            personLogicV2.sayMyName(name));
    }
}
