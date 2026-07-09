package org.goafabric.personservice.persistence

import org.goafabric.personservice.persistence.entity.PersonEo
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional


interface PersonRepository : CrudRepository<PersonEo, String> {
    fun findAll(example: Example<PersonEo>, pageable: Pageable): Page<PersonEo>

    //@EntityGraph(attributePaths = "address")
    fun findByAddressStreetContains(street: String, pageable: Pageable): Page<PersonEo>

    @Modifying
    @Transactional
    @Query("UPDATE PersonEo p SET p.firstName = :firstName WHERE p.lastName = :lastName")
    fun updateFirstNameByLastName(firstName: String, lastName: String): Int

    @Modifying
    @Transactional
    @Query("UPDATE person SET first_name = :firstName WHERE last_name = :lastName", nativeQuery = true)
    fun updateFirstNameByLastNameNative(firstName: String, lastName: String): Int

    /*
    @Transactional(propagation = Propagation.REQUIRES_NEW) //Workaround for getting the version increased, for JPARepository there is already a working method
    fun saveAndFlush(person: PersonEo): PersonEo {
        return save(person)
    }

     */
}