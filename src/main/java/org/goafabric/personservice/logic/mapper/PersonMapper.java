package org.goafabric.personservice.logic.mapper;

import org.goafabric.personservice.persistence.PersonBo;
import org.goafabric.personservice.service.Person;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface PersonMapper {
    Person map(PersonBo person);

    PersonBo map(Person person);

    List<Person> map(List<PersonBo> countries);
}
