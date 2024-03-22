package org.goafabric.personservice.logic;

import org.goafabric.personservice.persistence.entity.PersonEo;
import org.goafabric.personservice.controller.dto.Person;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    Person map(PersonEo value);

    PersonEo map(Person value);

    List<Person> map(List<PersonEo> value);

    List<Person> map(Iterable<PersonEo> value);
}
