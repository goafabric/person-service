package org.goafabric.personservice.persistence.extensions;

import org.goafabric.event.EventData;
import org.goafabric.personservice.extensions.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RegisterReflectionForBinding(EventData.class)
public class AuditTrailEventDispatcher {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, EventData> kafkaTemplate;
    private final String kafkaServers;

    public AuditTrailEventDispatcher(KafkaTemplate<String, EventData> kafkaTemplate, @Value("${spring.kafka.bootstrap-servers:}") String kafkaServers) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaServers = kafkaServers;
    }

    public void dispatchEvent(AuditTrailListener.AuditTrail auditTrail, Object payload) {
        if (!kafkaServers.isEmpty()) {
            log.info("producing event for type {}", payload.getClass().getSimpleName());
            kafkaTemplate.send("person", auditTrail.objectId(), new EventData(payload.getClass().getSimpleName(), auditTrail.operation().toString(), payload, UserContext.getAdapterHeaderMap()));
        }
    }

    /*
    record EventData(
            String type,
            String operation, //CREATE, UPDATE, DELETE
            Object payload,
            Map<String, String> tenantInfos
    ) {}
    ‘
     */
}
