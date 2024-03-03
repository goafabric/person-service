package org.goafabric.personservice.logic

import org.goafabric.personservice.adapter.CalleeServiceAdapter
import org.goafabric.personservice.controller.vo.Person
import org.goafabric.personservice.persistence.PersonRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*


@Component
@Transactional
class PersonLogic (
    private val personMapper : PersonMapper,
    private val personRepository: PersonRepository,
    private val calleeServiceAdapter: CalleeServiceAdapter) {

    fun getById(id: String) : Person {
        return personMapper.map(
            personRepository.findById(id).get())
    }

    fun findAll() : List<Person> {
        return personMapper.map(
            personRepository.findAll())
    }

    fun findByFirstName(firstName : String) : List<Person> {
        return personMapper.map(
            personRepository.findByFirstName(firstName))
    }

    fun findByLastName(lastName : String) : List<Person> {
        return personMapper.map(
            personRepository.findByLastName(lastName))
    }

    fun save(person : Person): Person? {
        return personMapper.map(personRepository.save(
                personMapper.map(person))
        )
    }

    fun findByStreet(street: String?): List<Person> {
        return personMapper.map(
            personRepository.findByAddress_StreetContainsIgnoreCase(street)
        )
    }

    fun sayMyName (name : String) : Person {
        return Person(firstName = calleeServiceAdapter.sayMyName(name)!!.message, lastName = "", address = emptyList())
    }
}