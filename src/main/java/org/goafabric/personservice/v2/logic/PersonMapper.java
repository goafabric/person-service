package org.goafabric.personservice.v2.logic;

import org.goafabric.personservice.base.persistence.entity.PersonEo;
import org.goafabric.personservice.v2.controller.dto.Person;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(implementationName = "PersonMapperV2Impl", componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    Person map(PersonEo value);

    PersonEo map(Person value);

    List<Person> map(List<PersonEo> value);

    List<Person> map(Iterable<PersonEo> value);
}
