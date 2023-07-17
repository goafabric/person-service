package org.goafabric.personservice.logic

import org.goafabric.personservice.persistence.domain.PersonEo
import org.goafabric.personservice.controller.vo.Person
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface PersonMapper {
    fun map(person: PersonEo): Person
    fun map(person: Person): PersonEo
    fun map(countries: List<PersonEo>): List<Person>
}