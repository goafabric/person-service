package org.goafabric.personservice.logic;

import org.goafabric.personservice.adapter.CalleeServiceAdapter;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.controller.dto.PersonSearch;
import org.goafabric.personservice.persistence.PersonRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
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

    public List<Person> find(PersonSearch personSearch, Integer page, Integer size) {
        /*
        var spec = Specification.where((Specification<PersonEo>) (root, query, criteriaBuilder)
                    -> personSearch.firstName() == null ? null : criteriaBuilder.equal(root.get("firstName"), personSearch.firstName()))
                        .and((Specification<PersonEo>) (root, query, criteriaBuilder)
                                -> personSearch.lastName() == null ? null : criteriaBuilder.equal(root.get("lastName"), personSearch.lastName()));
        */
        return personMapper.map(
                personRepository.findAll(
                        Example.of(personMapper.map(personSearch)),
                        PageRequest.of(page,size)));

    }

    public List<Person> findByStreet(String street, Integer page, Integer size) {
        return personMapper.map(
                personRepository.findByAddressStreetContainsIgnoreCase(street, PageRequest.of(page, size)));
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
