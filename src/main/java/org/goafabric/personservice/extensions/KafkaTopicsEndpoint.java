package org.goafabric.personservice.extensions;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Endpoint(id = "topics")
public class KafkaTopicsEndpoint {

    private final KafkaAdmin kafkaAdmin;

    public KafkaTopicsEndpoint(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @ReadOperation
    public Set<String> topics() throws Exception {
        try (AdminClient client =
                     AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            return client
                    .listTopics(new ListTopicsOptions().listInternal(false))
                    .names()
                    .get();
        }
    }
}

