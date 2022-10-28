package org.goafabric.personservice.logic

import org.goafabric.personservice.persistence.domain.PersonBo
import org.goafabric.personservice.controller.dto.Person
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface PersonMapper {
    fun map(person: PersonBo): Person
    fun map(person: Person): PersonBo
    fun map(countries: List<PersonBo>): List<Person>
}