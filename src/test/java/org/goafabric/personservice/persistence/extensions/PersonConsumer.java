package org.goafabric.personservice.persistence.extensions;

import org.goafabric.personservice.persistence.entity.AddressEo;
import org.goafabric.personservice.persistence.entity.PersonEo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Component
@KafkaListener(topics = {"person"}, groupId = "person")
public class PersonConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CountDownLatch latch = new CountDownLatch(1);

    @KafkaHandler
    public void consumePerson(PersonEo person, @Header("operation") String operation, @Headers Map<String, Object> headers) {
        log.info("loopback event for person {} {}", person, operation);
        latch.countDown();
    }

    @KafkaHandler
    public void consumeAddress(AddressEo address, @Header("operation") String operation, @Headers Map<String, Object> headers) {
        log.info("loopback event for address {} {}", address, operation);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}