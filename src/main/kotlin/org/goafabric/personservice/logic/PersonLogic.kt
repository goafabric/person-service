package org.goafabric.personservice.logic

import org.goafabric.personservice.adapter.CalleeServiceAdapter
import org.goafabric.personservice.persistence.PersonRepository
import org.goafabric.personservice.controller.vo.Person
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@Component
@Transactional
class PersonLogic (
    private val personMapper : PersonMapper,
    private val personRepository: PersonRepository,
    private val calleeServiceAdapter: CalleeServiceAdapter) {

    fun getById(@PathVariable("id") id: String) : Person {
        return personMapper.map(
            personRepository.findById(id).get())
    }

    @GetMapping("findAll")
    fun findAll() : List<Person> {
        return personMapper.map(
            personRepository.findAll())
    }

    @GetMapping("findByFirstName")
    fun findByFirstName(@RequestParam("firstName") firstName : String) : List<Person> {
        return personMapper.map(
            personRepository.findByFirstName(firstName))
    }

    @GetMapping("findByLastName")
    fun findByLastName(@RequestParam("lastName") lastName : String) : List<Person> {
        return personMapper.map(
            personRepository.findByLastName(lastName))
    }

    @PostMapping(value = ["save"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun save(@RequestBody @Valid person : Person): Person? {
        return personMapper.map(personRepository.save(
                personMapper.map(person))
        )
    }

    @GetMapping("sayMyName")
    fun sayMyName (@RequestParam("name") name : String) : Person {
        return Person(firstName = calleeServiceAdapter.sayMyName(name)!!.message, lastName = "", address = emptyList())
    }
}