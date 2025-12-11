package org.goafabric.personservice.extensions;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RegisterReflection(classes = SimpleCircuitBreakerAspect.class, memberCategories = MemberCategory.INVOKE_DECLARED_METHODS)
public class SimpleCircuitBreakerAspect {
    @Around("@within(circuitBreaker) || @annotation(circuitBreaker)")
    public Object resolveTenantInfo(ProceedingJoinPoint joinPoint, CircuitBreaker circuitBreaker) throws Throwable {
        System.out.println("### inside simple Circuit Breaker Aspect");
        return joinPoint.proceed();
    }
}

