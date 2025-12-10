package org.goafabric.personservice.persistence.extensions

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.controller.PersonController
import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.goafabric.personservice.persistence.PersonRepository
import org.goafabric.personservice.persistence.extensions.AuditTrailListener.AuditTrail
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.List

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class AuditTrailListenerIT(
    @Autowired val personController: PersonController,
    @Autowired val personRepository: PersonRepository,
    @Autowired val entityManager: EntityManager) {

    @Test
    fun creatUpdateDeletePerson() {
        val person = save()

        val createPerson = selectFrom("CREATE", person.id)
        assertThat(createPerson.oldValue).isNull()
        assertThat(createPerson.newValue).isNotNull()
        assertThat(Objects.requireNonNull(createPerson.newValue))
            .isNotNull().contains("Homer", "Simpson")

        val updatePerson = selectFrom("UPDATE", person.id)
        assertThat(updatePerson.oldValue).isNotNull()
        assertThat(updatePerson.oldValue).isNotNull()
        assertThat(Objects.requireNonNull(updatePerson.oldValue))
            .isNotNull().contains("Homer", "Simpson")
        assertThat(Objects.requireNonNull(updatePerson.newValue))
            .isNotNull().contains("updatedFirstName", "updatedLastName")

        val deletePerson = selectFrom("DELETE", person.id)
        assertThat(deletePerson.oldValue).isNotNull()
        assertThat(deletePerson.newValue).isNull()
        assertThat(Objects.requireNonNull(deletePerson.oldValue))
            .isNotNull().contains("updatedFirstName", "updatedLastName")
    }

    @Test
    fun creatUpdateDeleteAddress() {
        val address = save().address.first()

        val createAddress = selectFrom("CREATE", address?.id)
        assertThat(createAddress.oldValue).isNull()
        assertThat(createAddress.newValue).isNotNull()
        assertThat(Objects.requireNonNull(createAddress.newValue))
            .isNotNull().contains("Terrace")


        /*
        var updateAddress = selectFrom("UPDATE", address.id());
        assertThat(updateAddress.oldValue).isNotNull();
        assertThat(updateAddress.newValue).isNotNull();

         */
        val deleteAddress = selectFrom("DELETE", address?.id)
        assertThat(deleteAddress.oldValue).isNotNull()
        assertThat(deleteAddress.newValue).isNull()
        assertThat(Objects.requireNonNull(deleteAddress.oldValue))
            .isNotNull().contains("Terrace")
    }

    private fun selectFrom(operation: String, id: String?): AuditTrail {
        val query = entityManager!!.createQuery<AuditTrail>(
            "SELECT a FROM AuditTrailListener\$AuditTrail a WHERE a.objectId = :objectId AND a.operation = :operation",
            AuditTrail::class.java
        )
        query.setParameter("objectId", id)
        query.setParameter("operation", AuditTrailListener.DbOperation.valueOf(operation))
        return query.getSingleResult()
    }

    fun save(): Person {
        val person = personController!!.save(
            Person(
                null,
                null,
                "Homer",
                "Simpson",
                List.of<Address?>(
                    createAddress("Evergreen Terrace"),
                    createAddress("Everblue Terrace")
                )
            )
        )

        //update
        personController.save(
            Person(
                person.id, person.version,
                "updatedFirstName", "updatedLastName", person.address
            )
        )

        personRepository!!.deleteById(person.id!!)
        return person
    }

    private fun createAddress(street: String): Address {
        return Address(
            null, null,
            street, "Springfield"
        )
    }
}