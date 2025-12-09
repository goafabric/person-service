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
        assertThat(createPerson.getOldvalue()).isNull();
        assertThat(createPerson.getNewvalue()).isNotNull();
        assertThat(Objects.requireNonNull(createPerson.getNewvalue()))
                .isNotNull().contains("Homer", "Simpson");

        var updatePerson = selectFrom("UPDATE", person.id());
        assertThat(updatePerson.getOldvalue()).isNotNull();
        assertThat(updatePerson.getOldvalue()).isNotNull();
        assertThat(Objects.requireNonNull(updatePerson.getOldvalue()))
                .isNotNull().contains("Homer", "Simpson");
        assertThat(Objects.requireNonNull(updatePerson.getNewvalue()))
                .isNotNull().contains("updatedFirstName", "updatedLastName");

        var deletePerson = selectFrom("DELETE", person.id());
        assertThat(deletePerson.getOldvalue()).isNotNull();
        assertThat(deletePerson.getNewvalue()).isNull();
        assertThat(Objects.requireNonNull(deletePerson.getOldvalue()))
                .isNotNull().contains("updatedFirstName", "updatedLastName");
    }

    @Test
    public void creatUpdateDeleteAddress() {
        var address = save().address().getFirst();

        var createAddress = selectFrom("CREATE", address.id());
        assertThat(createAddress.getOldvalue()).isNull();
        assertThat(createAddress.getNewvalue()).isNotNull();
        assertThat(Objects.requireNonNull(createAddress.getNewvalue()))
                .isNotNull().contains("Terrace");


        /*
        var updateAddress = selectFrom("UPDATE", address.id());
        assertThat(updateAddress.getOldvalue()).isNotNull();
        assertThat(updateAddress.getNewvalue()).isNotNull();

         */

        var deleteAddress = selectFrom("DELETE", address.id());
        assertThat(deleteAddress.getOldvalue()).isNotNull();
        assertThat(deleteAddress.getNewvalue()).isNull();
        assertThat(Objects.requireNonNull(deleteAddress.getOldvalue()))
                .isNotNull().contains("Terrace");

    }


    /*
    @NotNull
    private Map<String, @Nullable Object> selectFrom(String operation, String id) {
        var tableName = schemaPrefix + UserContext.getTenantId() + ".audit_trail";
        var map = jdbcTemplate.queryForMap(
                "select * from %s where object_id = '%s' and operation = '%s'"
                        .formatted(tableName, id, operation));
        return map;
    }
    
     */

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
                        "Homer",
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