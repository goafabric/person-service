/*
package org.goafabric.personservice.controller;

import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@KafkaListener(topics = {"person"}, groupId = "person-2")
public class PersonConsumerProd {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CountDownLatch latch = new CountDownLatch(1);

    @KafkaHandler
    public void consumePerson(Person person, @Header("operation") String operation) {
        //if (true) { throw new NullPointerException(); }

        log.info("loopback event for person {} {}", person, operation);
        latch.countDown();
    }

    @KafkaHandler
    public void consumeAddress(Address address, @Header("operation") String operation) {
        log.info("loopback event for address {} {}", address, operation);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
*/