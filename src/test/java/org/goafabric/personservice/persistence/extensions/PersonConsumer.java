package org.goafabric.personservice.persistence.extensions;

import org.goafabric.personservice.persistence.entity.AddressEo;
import org.goafabric.personservice.persistence.entity.PersonEo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
@KafkaListener(topics = {"person"}, groupId = "person")
public class PersonConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CountDownLatch latch = new CountDownLatch(1);

    @KafkaHandler
    public void listen(PersonEo person) {
        log.info("loopback event {}", person);
        latch.countDown();
    }

    @KafkaHandler
    public void listen2(AddressEo address) {
        log.info("loopback event {}", address);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}