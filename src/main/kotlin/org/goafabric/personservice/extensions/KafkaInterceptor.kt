package org.goafabric.personservice.extensions

import io.opentelemetry.api.trace.Span
import io.opentelemetry.context.Context
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.goafabric.personservice.controller.dto.EventData
import org.slf4j.MDC
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Aspect
@Component
@RegisterReflection(classes = [KafkaInterceptor::class], memberCategories = [MemberCategory.INVOKE_DECLARED_METHODS])
class KafkaInterceptor {
    @Around("@annotation(kafkaListener) && args(..,eventData)")
    @Throws(Throwable::class)
    fun resolveTenantInfo(joinPoint: ProceedingJoinPoint, kafkaListener: KafkaListener, eventData: EventData): Any {
        UserContext.setContext(
            eventData.tenantInfos.get("X-TenantId"), eventData.tenantInfos.get("X-OrganizationId"),
            eventData.tenantInfos.get("X-Auth-Request-Preferred-Username"), eventData.tenantInfos.get("X-UserInfo")
        )

        configureLogsAndTracing()
        val result = joinPoint.proceed()
        afterCompletion()

        return result
    }

    private fun configureLogsAndTracing() {
        Span.fromContext(Context.current()).setAttribute("tenant.id", UserContext.tenantId)
        MDC.put("tenantId", UserContext.tenantId)
    }

    private fun afterCompletion() {
        UserContext.removeContext()
        MDC.remove("tenantId")
    }
}