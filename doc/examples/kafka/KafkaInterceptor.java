package org.goafabric.personservice.extensions;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.util.backoff.FixedBackOff;

import java.nio.charset.StandardCharsets;

@Configuration
public class KafkaInterceptor {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory (
            ConsumerFactory<String, Object> consumerFactory,
            RecordInterceptor<String, Object> recordInterceptor,
            DefaultErrorHandler deadLetterErrorHandler
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.setRecordInterceptor(recordInterceptor);
        factory.setCommonErrorHandler(deadLetterErrorHandler);
        return factory;
    }

    @Bean
    public RecordInterceptor<String, Object> recordInterceptor() {
        return new RecordInterceptor<>() {
            @Override
            public ConsumerRecord<String, Object> intercept(ConsumerRecord<String, Object> record, Consumer<String, Object> consumer) {
                UserContext.setContext(getValue(record.headers(), "X-TenantId"), getValue(record.headers(), "X-OrganizationId"),
                        getValue(record.headers(), "X-Auth-Request-Preferred-Username"), null);
                configureLogsAndTracing();
                return record;
            }

            @Override
            public void afterRecord(ConsumerRecord<String, Object> record, Consumer<String, Object> consumer) {
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
