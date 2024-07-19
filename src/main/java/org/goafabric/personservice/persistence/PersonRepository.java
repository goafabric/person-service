package org.goafabric.personservice.persistence;

import org.goafabric.personservice.persistence.entity.PersonEo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PersonRepository extends CrudRepository<PersonEo, String> {

    List<PersonEo> findByFirstName(String firstName);

    List<PersonEo> findByLastName(String lastName);

    @EntityGraph(attributePaths = "address")
    List<PersonEo> findByAddressStreetContainsIgnoreCase(String street);

}

