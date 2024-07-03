package org.goafabric.personservice.controller;

import jakarta.validation.Valid;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.logic.PersonLogic;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/persons", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Validated
public class PersonController {
    private final PersonLogic personLogic;

    public PersonController(PersonLogic personLogic) {
        this.personLogic = personLogic;
    }

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

    @GetMapping("findByStreet")
    public List<Person> findByStreet(String street) {
        return personLogic.findByStreet(street);
    }

    @PostMapping(value = "save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Person save(@RequestBody @Valid Person person) {
        return personLogic.save(person);
    }

    @GetMapping("sayMyName")
    public Person sayMyName (@RequestParam("name") String name) {
        return personLogic.sayMyName(name);
    }
}
