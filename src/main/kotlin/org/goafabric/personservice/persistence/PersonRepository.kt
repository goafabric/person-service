package org.goafabric.personservice.persistence

import org.goafabric.personservice.persistence.entity.PersonEo
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface PersonRepository : CrudRepository<PersonEo, String> {
    fun findAll(pageable: Pageable): Page<PersonEo>

    fun findByFirstName(firstName: String, pageable: Pageable): Page<PersonEo>

    @Query("SELECT p FROM PersonEo p where p.lastName = :lastName")
    fun findByLastName(@Param("lastName") lastName: String, pageable: Pageable): Page<PersonEo>

    //@EntityGraph(attributePaths = "address")
    fun findByAddressStreetContainsIgnoreCase(street: String, pageable: Pageable): Page<PersonEo>

}