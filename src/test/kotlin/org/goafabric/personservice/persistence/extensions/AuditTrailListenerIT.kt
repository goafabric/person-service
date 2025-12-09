package org.goafabric.personservice.persistence.extensions

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
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
internal class AuditTrailListenerIT {
    @Autowired
    private val personController: PersonController? = null

    @Autowired
    private val personRepository: PersonRepository? = null

    @PersistenceContext
    private val entityManager: EntityManager? = null

    @Test
    fun creatUpdateDeletePerson() {
        val person = save()

        val createPerson = selectFrom("CREATE", person.id)
        assertThat(createPerson.oldvalue).isNull()
        assertThat(createPerson.newvalue).isNotNull()
        assertThat(Objects.requireNonNull(createPerson.newvalue))
            .isNotNull().contains("Homer", "Simpson")

        val updatePerson = selectFrom("UPDATE", person.id)
        assertThat(updatePerson.oldvalue).isNotNull()
        assertThat(updatePerson.oldvalue).isNotNull()
        assertThat(Objects.requireNonNull(updatePerson.oldvalue))
            .isNotNull().contains("Homer", "Simpson")
        assertThat(Objects.requireNonNull(updatePerson.newvalue))
            .isNotNull().contains("updatedFirstName", "updatedLastName")

        val deletePerson = selectFrom("DELETE", person.id)
        assertThat(deletePerson.oldvalue).isNotNull()
        assertThat(deletePerson.newvalue).isNull()
        assertThat(Objects.requireNonNull(deletePerson.oldvalue))
            .isNotNull().contains("updatedFirstName", "updatedLastName")
    }

    @Test
    fun creatUpdateDeleteAddress() {
        val address = save().address.first()

        val createAddress = selectFrom("CREATE", address?.id)
        assertThat(createAddress.oldvalue).isNull()
        assertThat(createAddress.newvalue).isNotNull()
        assertThat(Objects.requireNonNull(createAddress.newvalue))
            .isNotNull().contains("Terrace")


        /*
        var updateAddress = selectFrom("UPDATE", address.id());
        assertThat(updateAddress.oldValue).isNotNull();
        assertThat(updateAddress.newValue).isNotNull();

         */
        val deleteAddress = selectFrom("DELETE", address?.id)
        assertThat(deleteAddress.oldvalue).isNotNull()
        assertThat(deleteAddress.newvalue).isNull()
        assertThat(Objects.requireNonNull(deleteAddress.oldvalue))
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