package org.goafabric.personservice.consumer;

import org.goafabric.personservice.controller.dto.Address;
import org.goafabric.personservice.controller.dto.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;


//Without EventData we can now simply consume the Objects itself => No more SafeJsonSerializer + Reflection Registration of Entities required
//Headers are handled by KafkaInterceptor
@Component
public class PersonConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = {"person"}, groupId = "person")
    public void consumePerson(Person person, @Header("operation") String operation) {
        log.info("loopback event for person {} {}", person, operation);
        latch.countDown();
    }

    @KafkaListener(topics = {"address"}, groupId = "address")
    public void consumeAddress(Address address, @Header("operation") String operation) {
        log.info("loopback event for address {} {}", address, operation);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
