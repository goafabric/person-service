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
class PersonController(private val personLogic: PersonLogic) {
    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: String): Person {
        return personLogic.getById(id)
    }

    @GetMapping
    fun findAll(@RequestParam("page") page: Int, @RequestParam("size") size: Int): List<Person> {
        return personLogic.findAll(page, size)
    }

    @GetMapping("by-first-name")
    fun findByFirstName(@RequestParam("firstName") firstName: String,
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): List<Person> {
        return personLogic.findByFirstName(firstName, page, size)
    }

    @GetMapping("by-street")
    fun findByStreet(
        @RequestParam("street") street: String,
        @RequestParam("page") page: Int,
        @RequestParam("size") size: Int
    ): List<Person> {
        return personLogic.findByStreet(street, page, size)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun save(@RequestBody person: @Valid Person): Person {
        return personLogic.save(person)
    }

    @GetMapping("say-my-name")
    fun sayMyName(@RequestParam("name") name: String): Person {
        return personLogic.sayMyName(name)
    }
}
