package org.goafabric.personservice.persistence.extensions;

import org.goafabric.personservice.controller.dto.EventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class PersonConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = {"person"}, groupId = "person")
    public void listen(EventData eventData) {
        log.info("loopback event {}", eventData);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}