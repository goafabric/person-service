package org.goafabric.personservice.controller

import jakarta.validation.Valid
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.controller.dto.PersonSearch
import org.goafabric.personservice.logic.PersonLogic
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RequestMapping(value = ["/persons"], produces = [MediaType.APPLICATION_JSON_VALUE])
@RestController
@Validated
class PersonController(private val personLogic: PersonLogic) {
    @GetMapping("{id}")
    fun getById(@PathVariable("id") id: String): Person {
        return personLogic.getById(id)
    }

    @GetMapping //ModelAttribute automatically applies RequestParams to the GetMapping, please note that there should be indexes inside the DB for every Attribute
    fun find(
        @ModelAttribute personSearch: PersonSearch,
        @RequestParam("page") page: Int, @RequestParam("size") size: Int
    ): List<Person> {
        return personLogic.find(personSearch, page, size)
    }

    @GetMapping("street")
    fun findByStreet(
        @RequestParam("street") street: String,
        @RequestParam("page") page: Int, @RequestParam("size") size: Int
    ): List<Person> {
        return personLogic.findByStreet(street, page, size)
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun save(@RequestBody person: @Valid Person): Person {
        return personLogic.save(person)
    }

    @GetMapping("name")
    fun sayMyName(@RequestParam("name") name: String): Person {
        return personLogic.sayMyName(name)
    }
}
