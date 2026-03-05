package org.goafabric.personservice.persistence.extensions

import org.goafabric.personservice.controller.dto.Address
import org.goafabric.personservice.controller.dto.Person
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

//Without EventData we can now simply consume the Objects itself => No more SafeJsonSerializer + Reflection Registration of Entities required
//Headers are handled by KafkaInterceptor
@Component
//@RetryableTopic(attempts = "3", dltTopicSuffix = ".DLT")
class PersonConsumer {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    val latch: CountDownLatch = CountDownLatch(1)

    @KafkaListener(topics = ["person"], groupId = "person")
    fun consumePerson(person: Person, @Header("operation") operation: String) {
        log.info("loopback event for person {} {}", person, operation)
        latch.countDown()
    }

    @KafkaListener(topics = ["address"], groupId = "address")
    fun consumeAddress(address: Address, @Header("operation") operation: String) {
        log.info("loopback event for address {} {}", address, operation)
        latch.countDown()
    }
}