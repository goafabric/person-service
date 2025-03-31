package org.goafabric.personservice.logic

import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.controller.dto.PersonSearch
import org.goafabric.personservice.persistence.entity.PersonEo
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface PersonMapper {
    fun map(person: PersonEo): Person
    fun map(person: Person): PersonEo
    fun map(countries: List<PersonEo>): List<Person>
    fun map(countries: Iterable<PersonEo>): List<Person>

    fun map(value: PersonSearch): PersonEo
}