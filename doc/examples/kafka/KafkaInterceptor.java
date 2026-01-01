/*
package org.goafabric.personservice.extensions;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Aspect
@Component
@RegisterReflection(classes = KafkaInterceptor.class, memberCategories = MemberCategory.INVOKE_DECLARED_METHODS)
public class KafkaInterceptor {

    @Around("@annotation(org.springframework.kafka.annotation.KafkaHandler) && args(..,headers)")
    public Object resolveTenantInfo(ProceedingJoinPoint joinPoint, @Headers Map<String, Object> headers) throws Throwable {
        UserContext.setContext(new String((byte[]) headers.get("X-TenantId"), StandardCharsets.UTF_8), new String((byte[])headers.get("X-OrganizationId"), StandardCharsets.UTF_8),
                new String((byte[]) headers.get("X-Auth-Request-Preferred-Username"), StandardCharsets.UTF_8), null);

        configureLogsAndTracing();
        Object result = joinPoint.proceed();
        afterCompletion();

        return result;
    }

    private static void configureLogsAndTracing() {
        Span.fromContext(Context.current()).setAttribute("tenant.id", UserContext.getTenantId());
        MDC.put("tenantId", UserContext.getTenantId());
    }

    private static void afterCompletion() {
        UserContext.removeContext();
        MDC.remove("tenantId");
    }

}

 */