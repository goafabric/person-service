package org.goafabric.personservice.persistence;

import org.goafabric.personservice.persistence.entity.AddressEo;
import org.springframework.data.repository.CrudRepository;

public interface AddressRepository extends CrudRepository<AddressEo, String> {

}

