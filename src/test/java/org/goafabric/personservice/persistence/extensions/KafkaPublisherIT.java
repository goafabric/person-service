
package org.goafabric.personservice.persistence.extensions;

import org.goafabric.personservice.controller.PersonController;
import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1)
@DirtiesContext
class KafkaPublisherIT {
    @Autowired
    private PersonController personController;

    @Autowired
    private PersonConsumer personConsumer;

    @Test
    void save() throws InterruptedException {
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


        //update
        var personUpdated = personController.save(new Person(person.id(), person.version(), person.firstName(), "updated", person.address()));
        assertThat(personUpdated.id()).isEqualTo(person.id());
        assertThat(personUpdated.version()).isEqualTo(1L);

        //assertThat(personConsumer.getLatch().await(10, TimeUnit.SECONDS)).isTrue();
    }

    private Address createAddress(String street) {
        return new Address(null, null,
                street, "Springfield");
    }
}
