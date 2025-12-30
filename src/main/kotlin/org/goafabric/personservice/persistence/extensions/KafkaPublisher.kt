package org.goafabric.personservice.persistence.extensions

import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove
import jakarta.persistence.PostUpdate
import org.goafabric.personservice.controller.dto.EventData
import org.goafabric.personservice.extensions.UserContext.adapterHeaderMap
import org.goafabric.personservice.persistence.entity.AddressEo
import org.goafabric.personservice.persistence.entity.PersonEo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@RegisterReflection(
    classes = [EventData::class, PersonEo::class, AddressEo::class],
    memberCategories = [MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS]
)
@Component
class KafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, EventData>,
    @param:Value("\${spring.kafka.bootstrap-servers:}"
    ) private val kafkaServers: String
) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostPersist
    fun afterCreate(entity: Any) {
        publish("CREATE", entity)
    }

    @PostUpdate
    fun afterUpdate(entity: Any) {
        publish("UPDATE", entity)
    }

    @PostRemove
    fun afterDelete(entity: Any) {
        publish("DELETE", entity)
    }

    private fun publish(operation: String, entity: Any) {
        if (kafkaServers.isEmpty()) {
            return
        }

        when (entity) {
            is PersonEo  -> publish("person", entity.id!!, operation, entity)
            is AddressEo -> publish("address", entity.id!!, operation, entity)
            else -> error("Type " + entity::class)
        }
    }

    private fun publish(type: String, key: String, operation: String, payload: Any) {
        log.info("publishing event of type {}", type)
        kafkaTemplate.send(
            type, key,
            EventData(type, operation, payload, adapterHeaderMap)
        )
    }
}
