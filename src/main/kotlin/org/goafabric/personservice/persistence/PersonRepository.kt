package org.goafabric.personservice.persistence

import org.goafabric.personservice.repository.entity.PersonEo
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param


interface PersonRepository : CrudRepository<PersonEo, String> {
    fun findByFirstName(firstName: String): List<PersonEo>

    @Query("SELECT p FROM PersonEo p where p.lastName = :lastName")
    fun findByLastName(@Param("lastName") lastName: String): List<PersonEo>

    //@EntityGraph(attributePaths = "address")
    fun findByAddress_StreetContainsIgnoreCase(street: String?): List<PersonEo>

}