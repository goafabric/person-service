package org.goafabric.personservice.persistence.extensions;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.goafabric.personservice.controller.dto.EventData;
import org.goafabric.personservice.persistence.entity.AddressEo;
import org.goafabric.personservice.persistence.entity.PersonEo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RegisterReflection(classes = {EventData.class, PersonEo.class, AddressEo.class} //every type we publish needs to be registered
        , memberCategories = { MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS})
@Component
public class KafkaPublisher {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String kafkaServers;

    public KafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, @Value("${spring.kafka.bootstrap-servers:}") String kafkaServers) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaServers = kafkaServers;
    }

    @PostPersist
    public void afterCreate(Object object)  {
        publish("CREATE", object);
    }

    @PostUpdate
    public void afterUpdate(Object object) {
        publish("UPDATE", object);
    }

    @PostRemove
    public void afterDelete(Object object) {
        publish("DELETE", object);
    }

    private void publish(String operation, Object object) {
       if (kafkaServers.isEmpty()) { return; }

        switch (object) {
            case PersonEo person ->
                    publish("person", person.getId(), operation, object);
            case AddressEo address ->
                    publish("person", address.getId(), operation, object);
            default -> throw new IllegalStateException("Type " + object.getClass());
        }
    }

    private void publish(String type, String key, String operation, Object payload) {
        log.info("publishing event of type {}", type);
        kafkaTemplate.send(type, key, payload);

    }

}
