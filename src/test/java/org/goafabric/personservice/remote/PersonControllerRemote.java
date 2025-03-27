package org.goafabric.personservice.remote;

import jakarta.validation.Valid;
import org.goafabric.personservice.controller.dto.Person;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

public interface PersonControllerRemote {

    @GetExchange("persons/{id}")
    Person getById(@PathVariable("id") String id);

    @GetExchange("persons")
    List<Person> findAll(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetExchange("persons/by-first-name")
    List<Person> findByFirstName(@RequestParam("firstName") String firstName, @RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetExchange("persons/by-street")
    List<Person> findByStreet(@RequestParam("street") String street, @RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @PostExchange(value = "persons")
    Person save(@RequestBody @Valid Person person);

    @GetExchange("persons/say-my-name")
    Person sayMyName (@RequestParam("name") String name);

}
