package org.goafabric.personservice.logic;

import lombok.NonNull;
import org.goafabric.personservice.adapter.CalleeServiceAdapter;
import org.goafabric.personservice.service.dto.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//@Transactional
public class PersonLogic {
    //@Autowired
    //PersonMapper personMapper;

    //@Autowired
    //PersonRepository personRepository;

    @Autowired
    CalleeServiceAdapter calleeServiceAdapter;

    public Person getById(@NonNull String id) {
        return null;
        //return personMapper.map(
          //      personRepository.findById(id).get());
    }

    public List<Person> findAll() {
        return null;
        //return personMapper.map(
          //      personRepository.findAll());
    }

    public List<Person> findByFirstName(@NonNull String firstName) {
        return null;
        //return personMapper.map(
          //      personRepository.findByFirstName(firstName));
    }

    public List<Person> findByLastName(@NonNull String lastName) {
        return null;
        //return personMapper.map(
          //      personRepository.findByLastName(lastName));
    }

    public Person save(@NonNull Person person) {
        return null;
        //return personMapper.map(personRepository.save(
          //      personMapper.map(person)));
    }

    public Person sayMyName(@NonNull String name) {
        return Person.builder().firstName(
                calleeServiceAdapter.sayMyName(name).getMessage()).build();
    }
}
