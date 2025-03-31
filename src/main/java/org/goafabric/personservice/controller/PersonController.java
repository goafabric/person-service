package org.goafabric.personservice.controller;

import jakarta.validation.Valid;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.controller.dto.PersonSearch;
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

    @GetMapping("/{id}")
    public Person getById(@PathVariable("id") String id) {
        return personLogic.getById(id);
    }

    @GetMapping
    public List<Person> findAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return personLogic.findAll(page, size);
    }

    @GetMapping("by-first-name")
    public List<Person> findByFirstName(@RequestParam("firstName") String firstName, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return personLogic.findByFirstName(firstName, page, size);
    }

    @GetMapping("by-street")
    public List<Person> findByStreet(@RequestParam("street") String street, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return personLogic.findByStreet(street, page, size);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Person save(@RequestBody @Valid Person person) {
        return personLogic.save(person);
    }

    @GetMapping("say-my-name")
    public Person sayMyName (@RequestParam("name") String name) {
        return personLogic.sayMyName(name);
    }

    @GetMapping("search")
    public List<Person> search (@ModelAttribute PersonSearch personSearch, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        System.out.println(personSearch);
        return personLogic.search(personSearch, page, size);
    }
}
