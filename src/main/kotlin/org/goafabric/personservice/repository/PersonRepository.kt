package org.goafabric.personservice.persistence

import org.goafabric.personservice.persistence.domain.PersonEo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PersonRepository : JpaRepository<PersonEo, String> {
    fun findByFirstName(firstName: String): List<PersonEo>

    @Query("SELECT p FROM PersonEo p where p.lastName = :lastName")
    fun findByLastName(@Param("lastName") lastName: String): List<PersonEo>
}