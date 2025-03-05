package org.goafabric.personservice.v2.logic;

import org.goafabric.personservice.base.adapter.CalleeServiceAdapter;
import org.goafabric.personservice.v2.controller.dto.Person;
import org.goafabric.personservice.base.persistence.PersonRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("PersonLogicV2")
@Transactional
public class PersonLogic {
    private final PersonMapper personMapper;

    private final PersonRepository personRepository;

    private final CalleeServiceAdapter calleeServiceAdapter;

    public PersonLogic(PersonMapper personMapper, PersonRepository personRepository, CalleeServiceAdapter calleeServiceAdapter) {
        this.personMapper = personMapper;
        this.personRepository = personRepository;
        this.calleeServiceAdapter = calleeServiceAdapter;
    }

    public Person getById(String id) {
        return personMapper.map(
                personRepository.findById(id).orElseThrow());
    }

    public List<Person> findAll() {
        return personMapper.map(
                personRepository.findAll());
    }

    public List<Person> findByGivenName(String givenName) {
        return personMapper.map(
                personRepository.findByGivenName(givenName));
    }

    public List<Person> findByFamilyName(String lastName) {
        return personMapper.map(
                personRepository.findByFamilyName(lastName));
    }

    public List<Person> findByStreet(String street) {
        return personMapper.map(
                personRepository.findByAddressStreetContainsIgnoreCase(street));
    }

    public Person save(Person person) {
        return personMapper.map(personRepository.saveAndFlush(
                personMapper.map(person)));
    }

    public Person sayMyName(String name) {
        return new Person(null, null,
                calleeServiceAdapter.sayMyName(name).message(), "", "", null);
    }
}
