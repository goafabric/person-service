package org.goafabric.personservice.extensions

import io.opentelemetry.api.trace.Span
import io.opentelemetry.context.Context
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.ListTopicsOptions
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Headers
import org.goafabric.personservice.extensions.UserContext.removeContext
import org.goafabric.personservice.extensions.UserContext.setContext
import org.goafabric.personservice.extensions.UserContext.tenantId
import org.slf4j.MDC
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.listener.RecordInterceptor
import java.nio.charset.StandardCharsets

@Configuration
@Endpoint(id = "topics")
class KafkaInterceptor(private val kafkaAdmin: KafkaAdmin) {
    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, Any>,
        recordInterceptor: RecordInterceptor<String, Any>
    ): ConcurrentKafkaListenerContainerFactory<String, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, Any>()
        factory.setConsumerFactory(consumerFactory)
        factory.setRecordInterceptor(recordInterceptor)
        return factory
    }

    @Bean
    fun recordInterceptor(): RecordInterceptor<String, Any> {
        return object : RecordInterceptor<String, Any> {
            override fun intercept(
                consumerRecord: ConsumerRecord<String, Any>,
                consumer: Consumer<String, Any>
            ): ConsumerRecord<String, Any> {
                setContext(
                    getValue(consumerRecord.headers(), "X-TenantId"), getValue(consumerRecord.headers(), "X-OrganizationId"),
                    getValue(consumerRecord.headers(), "X-Auth-Request-Preferred-Username"), null
                )
                configureLogsAndTracing()
                return consumerRecord
            }

            override fun afterRecord(consumerRecord: ConsumerRecord<String, Any>, consumer: Consumer<String, Any>) {
                afterCompletion()
            }
        }
    }

    @ReadOperation
    fun topics(): MutableSet<String>? {
        AdminClient.create(kafkaAdmin.getConfigurationProperties()).use { client ->
            return client.listTopics(ListTopicsOptions().listInternal(false)).names().get()
        }
    }

    private fun configureLogsAndTracing() {
        Span.fromContext(Context.current()).setAttribute("tenant.id", tenantId)
        MDC.put("tenantId", tenantId)
    }

    private fun afterCompletion() {
        removeContext()
        MDC.remove("tenantId")
    }

    private fun getValue(headers: Headers, key: String): String {
        return String(headers.lastHeader(key).value(), StandardCharsets.UTF_8)
    }
}
