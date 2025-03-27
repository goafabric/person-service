package org.goafabric.personservice.persistence;

import org.goafabric.personservice.persistence.entity.PersonEo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PersonRepository extends CrudRepository<PersonEo, String>, PagingAndSortingRepository<PersonEo, String> {

    @Transactional(propagation = Propagation.REQUIRES_NEW) //Workaround for getting the version increased, for JPARepository there is already a working method
    default PersonEo saveAndFlush(PersonEo person) { return save(person); }

    List<PersonEo> findByFirstName(String firstName, Pageable pageable);

    @EntityGraph(attributePaths = "address")
    List<PersonEo> findByAddressStreetContainsIgnoreCase(String street, Pageable pageable);

}

