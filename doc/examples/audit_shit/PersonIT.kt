package org.goafabric.personservice.persistence

import org.goafabric.personservice.controller.dto.PersonSearch
import org.goafabric.personservice.logic.PersonLogic
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonIT {
    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var personLogic: PersonLogic
    
    @Test
    fun test() {
        personRepository.updateFirstNameByLastName(firstName = "Montgomery", lastName = "Burns")
        var persons = personLogic.find(PersonSearch(firstName = "Montgomery"), page = 0, size = 10)
        val person = persons.get(0)
        person.apply { person.firstName = "nobody" }
        personLogic.save(person)
        var x = 5
    }
}