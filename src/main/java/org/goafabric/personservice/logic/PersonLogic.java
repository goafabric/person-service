package org.goafabric.personservice.logic;

import org.goafabric.personservice.adapter.CalleeServiceAdapter;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.persistence.PersonRepository;
import org.goafabric.personservice.persistence.entity.AddressEo;
import org.goafabric.personservice.persistence.entity.PersonEo;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
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

    @Autowired
    Javers javers;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<Person> findAll() {

        var persons = javers.findSnapshots( QueryBuilder.byClass(PersonEo.class).build());
        persons.forEach(snapshot -> log.info(javers.getJsonConverter().toJson(snapshot)));

        var addresses = javers.findSnapshots( QueryBuilder.byClass(AddressEo.class).build());
        addresses.forEach(snapshot -> log.info(javers.getJsonConverter().toJson(snapshot)));

        return personMapper.map(
                personRepository.findAll());
    }

    public List<Person> findByFirstName(String firstName) {
        return personMapper.map(
                personRepository.findByFirstName(firstName));
    }

    public List<Person> findByLastName(String lastName) {
        return personMapper.map(
                personRepository.findByLastName(lastName));
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
                calleeServiceAdapter.sayMyName(name).message(), "", null);
    }
}
