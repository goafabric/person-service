package org.goafabric.personservice.persistence.entity;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.goafabric.personservice.extensions.TenantContext;
import org.hibernate.Session;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Component;


@Aspect
@Component
@ImportRuntimeHints(FilterAspect.ApplicationRuntimeHints.class)
public class FilterAspect {

    private final EntityManager entityManager;

    public FilterAspect(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Before("execution(* org.springframework.data.repository.CrudRepository+.*(..))")
    public void applyFilter() {
        var session = entityManager.unwrap(Session.class);
        session.enableFilter("organizationFilter").setParameter("organizationId", TenantContext.getOrganizationId());
        //session.enableFilter("tenantFilter").setParameter("tenantId", TenantContext.getTenantId());
    }

    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(FilterAspect.class, MemberCategory.INVOKE_DECLARED_METHODS);
        }
    }
}
