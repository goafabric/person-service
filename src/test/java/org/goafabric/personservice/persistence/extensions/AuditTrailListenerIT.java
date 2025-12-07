package org.goafabric.personservice.persistence.extensions;

import org.goafabric.personservice.controller.PersonController;
import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.goafabric.personservice.extensions.UserContext;
import org.goafabric.personservice.persistence.PersonRepository;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuditTrailListenerIT {
    @Autowired
    private PersonController personController;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${multi-tenancy.schema-prefix:_}") String schemaPrefix;

    @Test
    public void creatUpdateDeletePerson() {
        var person = save();

        var createPerson = selectFrom("CREATE", person.id());
        assertThat(createPerson.get("oldValue")).isNull();
        assertThat(createPerson.get("newValue")).isNotNull();
        assertThat(Objects.requireNonNull(createPerson.get("newValue")).toString())
                .isNotNull().contains("Homer", "Simpson");

        var updatePerson = selectFrom("UPDATE", person.id());
        assertThat(updatePerson.get("oldValue")).isNotNull();
        assertThat(updatePerson.get("oldValue")).isNotNull();
        assertThat(Objects.requireNonNull(updatePerson.get("oldValue")).toString())
                .isNotNull().contains("Homer", "Simpson");
        assertThat(Objects.requireNonNull(updatePerson.get("newValue")).toString())
                .isNotNull().contains("updatedFirstName", "updatedLastName");

        var deletePerson = selectFrom("DELETE", person.id());
        assertThat(deletePerson.get("oldValue")).isNotNull();
        assertThat(deletePerson.get("newValue")).isNull();
        assertThat(Objects.requireNonNull(deletePerson.get("oldValue")).toString())
                .isNotNull().contains("updatedFirstName", "updatedLastName");
    }

    @NotNull
    private Map<String, @Nullable Object> selectFrom(String operation, String id) {
        var tableName = schemaPrefix + UserContext.getTenantId() + ".audit_trail";
        var map = jdbcTemplate.queryForMap(
                "select * from %s where object_id = '%s' and operation = '%s'"
                        .formatted(tableName, id, operation));
        return map;
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

        assertThat(person).isNotNull();

        var person2 = personController.getById(person.id());
        assertThat(person2).isNotNull();
        assertThat(person2.address()).hasSize(2);


        //update
        var personUpdated = personController.save(new Person(person.id(), person.version(), "updatedFirstName", "updatedLastName", person.address()));
        assertThat(personUpdated.id()).isEqualTo(person.id());
        assertThat(personUpdated.version()).isEqualTo(1L);

        personRepository.deleteById(person.id());
        return person;
    }

    private Address createAddress(String street) {
        return new Address(null, null,
                street, "Springfield");
    }

}