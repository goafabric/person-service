package org.goafabric.personservice.extensions;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.util.backoff.FixedBackOff;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Configuration
@Endpoint(id = "topics")
@SuppressWarnings("java:S2638")
public class KafkaInterceptor {
    private final KafkaAdmin kafkaAdmin;

    public KafkaInterceptor(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory (
            ConsumerFactory<String, Object> consumerFactory,
            RecordInterceptor<String, Object> recordInterceptor
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.setRecordInterceptor(recordInterceptor);
        return factory;
    }

    @Bean
    public RecordInterceptor<String, Object> recordInterceptor() {
        return new RecordInterceptor<>() {
            @Override
            public ConsumerRecord<String, Object> intercept(@NonNull ConsumerRecord<String, Object> consumerRecord, @NonNull Consumer<String, Object> consumer) {
                UserContext.setContext(getValue(consumerRecord.headers(), "X-TenantId"), getValue(consumerRecord.headers(), "X-OrganizationId"),
                        getValue(consumerRecord.headers(), "X-Auth-Request-Preferred-Username"), null);
                configureLogsAndTracing();
                return consumerRecord;
            }

            @Override
            public void afterRecord(@NonNull ConsumerRecord<String, Object> consumerRecord, @NonNull Consumer<String, Object> consumer) {
                afterCompletion();
            }
        };
    }

    @Bean
    public DefaultErrorHandler deadLetterErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        var recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate, (record, exception) ->
                new TopicPartition(
                        record.topic() + ".DLT",
                        record.partition()
                )
        );

        var errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
        errorHandler.addNotRetryableExceptions(IllegalStateException.class, IllegalArgumentException.class);

        return errorHandler;
    }

    @ReadOperation
    public Set<String> topics() throws ExecutionException, InterruptedException {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            return client.listTopics(new ListTopicsOptions().listInternal(false)).names().get();
        }
    }

    private void configureLogsAndTracing() {
        Span.fromContext(Context.current()).setAttribute("tenant.id", UserContext.getTenantId());
        MDC.put("tenantId", UserContext.getTenantId());
    }

    private void afterCompletion() {
        UserContext.removeContext();
        MDC.remove("tenantId");
    }

    private String getValue(Headers headers, String key) {
        return new String(headers.lastHeader(key).value(), StandardCharsets.UTF_8);
    }

}
