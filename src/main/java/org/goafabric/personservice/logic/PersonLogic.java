package org.goafabric.personservice.logic;

import org.goafabric.personservice.adapter.CalleeServiceAdapter;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.controller.dto.PersonSearch;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//@CircuitBreaker(name = "calleeservice")
public class PersonLogic {


    private final CalleeServiceAdapter calleeServiceAdapter;

    public PersonLogic(CalleeServiceAdapter calleeServiceAdapter) {

        this.calleeServiceAdapter = calleeServiceAdapter;
    }

    public Person getById(String id) {
        return null;
    }

    public List<Person> find(PersonSearch personSearch, Integer page, Integer size) {
      return null;
    }

    public List<Person> findByStreet(String street, Integer page, Integer size) {
       return null;
    }

    public Person save(Person person) {
       return null;
    }

    public Person sayMyName(String name) {
        return new Person(null, null,
                calleeServiceAdapter.sayMyName(name).message(), "", null);
    }

}
