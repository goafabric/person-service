package org.goafabric.personservice.service;

import lombok.extern.slf4j.Slf4j;
import org.goafabric.personservice.logic.PersonLogic;
import org.goafabric.personservice.service.dto.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/persons", produces = "application/json")
@RestController
@Slf4j
public class PersonService {
    @Autowired
    PersonLogic personLogic;

    @GetMapping("getById/{id}")
    public Person getById(@PathVariable("id") String id) {
        return personLogic.getById(id);
    }

    @GetMapping("findAll")
    public List<Person> findAll() {
        return personLogic.findAll();
    }

    @GetMapping("findByFirstName")
    public List<Person> findByFirstName(@RequestParam("firstName") String firstName) {
        return personLogic.findByFirstName(firstName);
    }

    @GetMapping("findByLastName")
    public List<Person> findByLastName(@RequestParam("lastName") String lastName) {
        return personLogic.findByLastName(lastName);
    }


    @PostMapping(value = "save", consumes = "application/json")
    public Person save(@RequestBody Person person) {
        return personLogic.save(person);
    }

    @GetMapping("sayMyName")
    public Person sayMyName (@RequestParam("name") String name) {
        return personLogic.sayMyName(name);
    }
}
