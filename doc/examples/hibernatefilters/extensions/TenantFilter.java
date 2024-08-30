package org.goafabric.personservice.persistence.extensions;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.flywaydb.core.Flyway;
import org.goafabric.personservice.extensions.TenantContext;
import org.hibernate.Session;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Component;


@Aspect
@Component
@ImportRuntimeHints(TenantFilter.ApplicationRuntimeHints.class)
public class TenantFilter {

    private final EntityManager entityManager;

    public TenantFilter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Before("execution(* org.springframework.data.repository.CrudRepository+.*(..))")
    public void applyFilter() {
        var session = entityManager.unwrap(Session.class);
        session.enableFilter("tenantFilter").setParameter("tenantId", TenantContext.getTenantId());
        session.enableFilter("tenantFilterOrg").setParameter("tenantId", TenantContext.getTenantId());
        session.enableFilter("organizationFilter").setParameter("organizationId", TenantContext.getOrganizationId());
    }

    @Configuration
    static class FlywayConfig {
        @Bean
        public FlywayMigrationStrategy flywayMigrationStrategy(@Value("${database.provisioning.goals}") String goals) {
            return goals.contains("-migrate") ? Flyway::migrate : flyway -> {
            };
        }
    }

    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(TenantFilter.class, MemberCategory.INVOKE_DECLARED_METHODS);
        }
    }

}
