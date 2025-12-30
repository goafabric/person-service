package org.goafabric.personservice.persistence.extensions

import org.goafabric.personservice.controller.dto.EventData
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

@Component
class PersonConsumer {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    val latch: CountDownLatch = CountDownLatch(1)

    @KafkaListener(
        topics = ["person"],
        groupId = "person"
    )
    fun listen(eventData: EventData) {
        log.info("loopback event {}", eventData.toString())
        latch.countDown()
    }
}