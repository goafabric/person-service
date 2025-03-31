package org.goafabric.personservice.logic

import org.goafabric.personservice.adapter.CalleeServiceAdapter
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.controller.dto.PersonSearch
import org.goafabric.personservice.persistence.PersonRepository
import org.springframework.data.domain.Example
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class PersonLogic(
    private val personMapper: PersonMapper,
    private val personRepository: PersonRepository,
    private val calleeServiceAdapter: CalleeServiceAdapter
) {
    fun getById(id: String): Person {
        return personMapper.map(
            personRepository.findById(id).orElseThrow()
        )
    }

    fun find(personSearch: PersonSearch, page: Int, size: Int): List<Person> {
        return personMapper.map(
            personRepository.findAll(
                Example.of(personMapper.map(personSearch)),
                PageRequest.of(page, size)
            )
        )
    }

    fun findByStreet(street: String, page: Int, size: Int): List<Person> {
        return personMapper.map(
            personRepository.findByAddressStreetContainsIgnoreCase(street, PageRequest.of(page, size))
        )
    }

    fun save(person : Person): Person {
        return personMapper.map(personRepository.save(
            personMapper.map(person))
        )
    }

    fun sayMyName (name : String) : Person {
        return Person(firstName = calleeServiceAdapter.sayMyName(name)!!.message, lastName = "", address = emptyList())
    }

}
