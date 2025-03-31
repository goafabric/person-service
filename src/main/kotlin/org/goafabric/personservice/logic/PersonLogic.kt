package org.goafabric.personservice.logic

import org.goafabric.personservice.adapter.CalleeServiceAdapter
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.persistence.PersonRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


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

    fun findAll(page: Int, size: Int): List<Person> {
        return personMapper.map(
            personRepository.findAll(PageRequest.of(page, size)))
    }

    fun findByFirstName(firstName: String, page: Int, size: Int) : List<Person> {
        return personMapper.map(
            personRepository.findByFirstName(firstName, PageRequest.of(page, size)))
    }

    fun save(person : Person): Person {
        return personMapper.map(personRepository.save(
                personMapper.map(person))
        )
    }

    fun findByStreet(street: String, page: Int, size: Int): List<Person> {
        return personMapper.map(
            personRepository.findByAddressStreetContainsIgnoreCase(street, PageRequest.of(page, size))
        )
    }

    fun sayMyName (name : String) : Person {
        return Person(firstName = calleeServiceAdapter.sayMyName(name)!!.message, lastName = "", address = emptyList())
    }
}