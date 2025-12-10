package org.goafabric.personservice.persistence.extensions;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.goafabric.personservice.controller.PersonController;
import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.persistence.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuditTrailListenerIT {
    @Autowired
    private PersonController personController;

    @Autowired
    private PersonRepository personRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void creatUpdateDeletePerson() {
        var person = save();

        var createPerson = selectFrom("CREATE", person.id());
        assertThat(createPerson.getOldValue()).isNull();
        assertThat(createPerson.getNewValue()).isNotNull();
        assertThat(Objects.requireNonNull(createPerson.getNewValue()))
                .isNotNull().contains("Marge", "Simpson");

        var updatePerson = selectFrom("UPDATE", person.id());
        assertThat(updatePerson.getOldValue()).isNotNull();
        assertThat(updatePerson.getOldValue()).isNotNull();
        assertThat(Objects.requireNonNull(updatePerson.getOldValue()))
                .isNotNull().contains("Marge", "Simpson");
        assertThat(Objects.requireNonNull(updatePerson.getNewValue()))
                .isNotNull().contains("updatedFirstName", "updatedLastName");

        var deletePerson = selectFrom("DELETE", person.id());
        assertThat(deletePerson.getOldValue()).isNotNull();
        assertThat(deletePerson.getNewValue()).isNull();
        assertThat(Objects.requireNonNull(deletePerson.getOldValue()))
                .isNotNull().contains("updatedFirstName", "updatedLastName");
    }

    @Test
    public void creatUpdateDeleteAddress() {
        var address = save().address().getFirst();

        var createAddress = selectFrom("CREATE", address.id());
        assertThat(createAddress.getOldValue()).isNull();
        assertThat(createAddress.getNewValue()).isNotNull();
        assertThat(Objects.requireNonNull(createAddress.getNewValue()))
                .isNotNull().contains("Terrace");


        /*
        var updateAddress = selectFrom("UPDATE", address.id());
        assertThat(updateAddress.getOldvalue()).isNotNull();
        assertThat(updateAddress.getNewvalue()).isNotNull();

         */

        var deleteAddress = selectFrom("DELETE", address.id());
        assertThat(deleteAddress.getOldValue()).isNotNull();
        assertThat(deleteAddress.getNewValue()).isNull();
        assertThat(Objects.requireNonNull(deleteAddress.getOldValue()))
                .isNotNull().contains("Terrace");

    }

    private AuditTrailListener.AuditTrail selectFrom(String operation, String id) {
        var query = entityManager.createQuery(
                "SELECT a FROM AuditTrailListener$AuditTrail a WHERE a.objectId = :objectId AND a.operation = :operation", AuditTrailListener.AuditTrail.class);
        query.setParameter("objectId", id);
        query.setParameter("operation", AuditTrailListener.DbOperation.valueOf(operation));
        return query.getSingleResult();
    }

    Person save() {
        final Person person = personController.save(
                new Person(null,
                        null,
                        "Marge",
                        "Simpson",
                        List.of(
                                createAddress("Evergreen Terrace"),
                                createAddress("Everblue Terrace"))
                ));

        //update
        personController.save(new Person(person.id(), person.version(),
                "updatedFirstName", "updatedLastName", person.address()));

        personRepository.deleteById(person.id());
        return person;
    }

    private Address createAddress(String street) {
        return new Address(null, null,
                street, "Springfield");
    }

}