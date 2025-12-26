package org.goafabric.personservice.persistence.extensions

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.controller.PersonController
import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.util.concurrent.TimeUnit

@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1)
@DirtiesContext
class KafkaPublisherIT(
    @Autowired private val personController: PersonController,
    @Autowired private val personConsumer: PersonConsumer) {

    @Test
    @Throws(InterruptedException::class)
    fun save() {
        val person = personController!!.save(
            Person(
                null,
                null,
                "Homer",
                "Simpson",
                listOf<Address?>(
                    createAddress("Evergreen Terrace"),
                    createAddress("Everblue Terrace")
                )
            )
        )

        Assertions.assertThat<Person?>(person).isNotNull()


        //update
        val personUpdated = personController.save(
            Person(
                person.id,
                person.version,
                person.firstName,
                "updated",
                person.address
            )
        )
        assertThat(personUpdated.id).isEqualTo(person.id)

        Assertions.assertThat(personConsumer.latch.await(10, TimeUnit.SECONDS))
    }

    private fun createAddress(street: String): Address {
        return Address(
            null, null,
            street, "Springfield"
        )
    }
}
