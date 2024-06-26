package org.goafabric.personservice.persistence.extensions;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.goafabric.personservice.extensions.TenantContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

//Extra Code to set the TenantId for verification inside the database, this code is unverified !
@Aspect
@Component
@ImportRuntimeHints(TenantConnectionAspect.ApplicationRuntimeHints.class)
public class TenantConnectionAspect {
    private final JdbcTemplate jdbcTemplate;

    public TenantConnectionAspect(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Before("execution(* javax.sql.DataSource.getConnection(..))")
    public void setSessionParameter() {
        jdbcTemplate.update("SET app.tenant_id = ?", TenantContext.getTenantId());
    }

    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(TenantConnectionAspect.class, MemberCategory.INVOKE_DECLARED_METHODS);
        }
    }

}