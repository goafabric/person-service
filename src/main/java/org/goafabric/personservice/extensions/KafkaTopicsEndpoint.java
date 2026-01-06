package org.goafabric.personservice.extensions;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartition;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Endpoint(id = "topics")
public class KafkaTopicsEndpoint {

    private final KafkaAdmin kafkaAdmin;

    public KafkaTopicsEndpoint(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    /*
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

     */

    public record TopicStats(int partitions, long messageCount) {}

    @ReadOperation
    public Map<String, TopicStats> topics() throws Exception {

        try (AdminClient client =
                     AdminClient.create(kafkaAdmin.getConfigurationProperties())) {

            // list topics
            Set<String> topics = client.listTopics(
                    new ListTopicsOptions().listInternal(false)
            ).names().get();

            // describe topics
            Map<String, TopicDescription> desc =
                    client.describeTopics(topics).allTopicNames().get();

            // build partitions
            List<TopicPartition> tps = desc.values().stream()
                    .flatMap(d -> d.partitions().stream()
                            .map(p -> new TopicPartition(d.name(), p.partition())))
                    .toList();

            // offsets
            Map<TopicPartition, Long> earliest = client
                    .listOffsets(tps.stream()
                            .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.earliest())))
                    .all().get()
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().offset()));

            Map<TopicPartition, Long> latest = client
                    .listOffsets(tps.stream()
                            .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.latest())))
                    .all().get()
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> e.getValue().offset()));

            // aggregate
            Map<String, Long> counts = new HashMap<>();
            for (TopicPartition tp : tps) {
                counts.merge(tp.topic(),
                        Math.max(latest.get(tp) - earliest.get(tp), 0),
                        Long::sum);
            }

            return desc.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new TopicStats(
                                    e.getValue().partitions().size(),
                                    counts.getOrDefault(e.getKey(), 0L)
                            ),
                            (a, b) -> a,
                            TreeMap::new
                    ));
        }
    }

}

