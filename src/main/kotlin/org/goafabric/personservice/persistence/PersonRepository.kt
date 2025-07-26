package org.goafabric.personservice.persistence

import org.goafabric.personservice.persistence.entity.PersonEo
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional


interface PersonRepository : CrudRepository<PersonEo, String> {
    fun findAll(example: Example<PersonEo>, pageable: Pageable): Page<PersonEo>

    //@EntityGraph(attributePaths = "address")
    fun findByAddressStreetContains(street: String, pageable: Pageable): Page<PersonEo>

    @Transactional(propagation = Propagation.REQUIRES_NEW) //Workaround for getting the version increased, for JPARepository there is already a working method
    fun saveAndFlush(person: PersonEo): PersonEo {
        return save(person)
    }
}