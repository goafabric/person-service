package org.goafabric.personservice.persistence

import org.goafabric.personservice.persistence.domain.PersonBo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PersonRepository : JpaRepository<PersonBo, String> {
    fun findByFirstName(firstName: String): List<PersonBo>

    @Query("SELECT p FROM PersonBo p where p.lastName = :lastName")
    fun findByLastName(@Param("lastName") lastName: String): List<PersonBo>
}