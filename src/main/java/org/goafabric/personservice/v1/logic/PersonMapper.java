package org.goafabric.personservice.v1.logic;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonMapper {
    org.goafabric.personservice.v1.controller.dto.Person map(org.goafabric.personservice.v2.controller.dto.Person value);

    org.goafabric.personservice.v2.controller.dto.Person map(org.goafabric.personservice.v1.controller.dto.Person value);

    List<org.goafabric.personservice.v1.controller.dto.Person> map(List<org.goafabric.personservice.v2.controller.dto.Person> value);

}
