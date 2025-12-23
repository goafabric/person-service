package org.goafabric.personservice.persistence.extensions;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.goafabric.personservice.extensions.UserContext;
import org.goafabric.personservice.persistence.entity.AddressEo;
import org.goafabric.personservice.persistence.entity.PersonEo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@RegisterReflection(classes = {KafkaListener.EventData.class, PersonEo.class, AddressEo.class} //every type we publish needs to be registered
        , memberCategories = { MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS})
public class KafkaListener implements ApplicationContextAware {

    private ApplicationContext context;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    record EventData(String type, String operation, Object payload, Map<String, String> tenantInfos) {}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
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
        if (context.getEnvironment().getProperty("spring.kafka.bootstrap-servers") == null) { return; }

        switch (object) {
            case PersonEo person ->
                    publish("person", person.getId(), operation, object);
            case AddressEo address ->
                    publish("address", address.getId(), operation, object);
            default -> throw new IllegalStateException("Type " + object.getClass());
        }
    }

    private void publish(String type, String key, String operation, Object payload) {
        log.info("publishing event of type {}", type);
        kafkaTemplate().send(type, key,
                new EventData(type, operation, payload, UserContext.getAdapterHeaderMap()));
    }

    private KafkaTemplate<String, Object> kafkaTemplate() { return context.getBean(KafkaTemplate.class); }
}
