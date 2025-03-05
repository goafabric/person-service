package org.goafabric.personservice.v1.logic;

import org.goafabric.personservice.v1.controller.dto.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(implementationName = "PersonMapperV1Impl", componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    @Mapping(target = "firstName", source = "value.givenName")
    @Mapping(target = "lastName", source = "value.familyName")
    org.goafabric.personservice.v1.controller.dto.Person map(org.goafabric.personservice.v2.controller.dto.Person value);

    @Mapping(target = "givenName", source = "value.firstName")
    @Mapping(target = "familyName", source = "value.lastName")
    org.goafabric.personservice.v2.controller.dto.Person map(org.goafabric.personservice.v1.controller.dto.Person value);

    List<Person> map(List<org.goafabric.personservice.v2.controller.dto.Person> value);

}
