package org.goafabric.personservice.extensions;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Endpoint(id = "topics")
@Component
public class KafkaActuator {
    private final KafkaAdmin kafkaAdmin;
    private final KafkaProperties kafkaProperties;

    public KafkaActuator(KafkaAdmin kafkaAdmin, KafkaProperties kafkaProperties) {
        this.kafkaAdmin = kafkaAdmin;
        this.kafkaProperties = kafkaProperties;
    }

    @ReadOperation
    public Map<String, List<String>> kafkaMessages()
            throws ExecutionException, InterruptedException {

        Map<String, List<String>> result = new HashMap<>();

        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            var topics = admin
                    .listTopics(new ListTopicsOptions().listInternal(true))
                    .names()
                    .get();

            var consumerProps = kafkaProperties.buildConsumerProperties();
            consumerProps.putAll(kafkaProperties.getProperties());
            consumerProps.putAll(kafkaAdmin.getConfigurationProperties());
            consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "debug-latest-2");
            consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

            try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
                for (String topic : topics) {
                    List<String> messages = new ArrayList<>();

                    // Get partitions for topic
                    var partitions = consumer.partitionsFor(topic).stream().map(p -> new TopicPartition(topic, p.partition())).toList();
                    if (partitions.isEmpty()) {
                        result.put(topic, messages);
                        continue;
                    }
                    consumer.assign(partitions);

                    // Get end offsets and Seek to (end - 2)
                    var endOffsets = consumer.endOffsets(partitions);
                    for (TopicPartition tp : partitions) {
                        consumer.seek(tp, Math.max(endOffsets.get(tp) - 2, 0));
                    }

                    // Poll records
                    var records = consumer.poll(Duration.ofSeconds(2));
                    records.forEach(record ->
                            messages.add(String.format("partition=%d offset=%d key=%s payload=%s", record.partition(), record.offset(), record.key(), record.value())
                            ));

                    result.put(topic, messages);
                }
            }
        }

        return result;
    }

}
