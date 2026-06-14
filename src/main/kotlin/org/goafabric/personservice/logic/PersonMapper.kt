package org.goafabric.personservice.logic

import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.controller.dto.PersonSearch
import org.goafabric.personservice.persistence.entity.AddressEo
import org.goafabric.personservice.persistence.entity.PersonEo
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface PersonMapper {
    fun map(value: PersonEo): Person
    fun map(value: Person): PersonEo
    fun map(values: List<PersonEo>): List<Person>
    fun map(values: Iterable<PersonEo>): List<Person>

    fun map(value: PersonSearch): PersonEo

    fun map (value: AddressEo): Address
}