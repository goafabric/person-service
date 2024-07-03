package org.goafabric.personservice.controller

import jakarta.validation.Valid
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.logic.PersonLogic
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RequestMapping(value = ["/persons"], produces = [MediaType.APPLICATION_JSON_VALUE])
@RestController
@Validated
class PersonController (
    private val personLogic : PersonLogic) {

    @GetMapping("getById/{id}")
    fun getById(@PathVariable("id") id: String) : Person {
        return personLogic.getById(id);
    }

    @GetMapping("findAll")
    fun findAll() : List<Person> {
        return personLogic.findAll();
    }

    @GetMapping("findByFirstName")
    fun findByFirstName(@RequestParam("firstName") firstName : String) : List<Person> {
        return personLogic.findByFirstName(firstName);
    }

    @GetMapping("findByLastName")
    fun findByLastName(@RequestParam("lastName") lastName : String) : List<Person> {
        return personLogic.findByLastName(lastName);
    }

    @GetMapping("findByStreet")
    fun findByStreet(street: String?): List<Person> {
        return personLogic.findByStreet(street)
    }

    @PostMapping(value = ["save"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun save(@RequestBody @Valid person : Person): Person? {
        return personLogic.save(person);
    }

    @GetMapping("sayMyName")
    fun sayMyName (@RequestParam("name") name : String) : Person {
        return personLogic.sayMyName(name);
    }
}