package org.goafabric.personservice.persistence;

import org.goafabric.personservice.persistence.entity.PersonEo;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface PersonRepository extends CrudRepository<PersonEo, String> {

    Page<PersonEo> findAll(Example<PersonEo> example, Pageable pageable);

    @EntityGraph(attributePaths = "address")
    Page<PersonEo> findByAddressStreetContains(String street, Pageable pageable);

    @Transactional(propagation = Propagation.REQUIRES_NEW) //Workaround for getting the version increased, for JPARepository there is already a working method
    default PersonEo saveAndFlush(PersonEo person) { return save(person); }
}

