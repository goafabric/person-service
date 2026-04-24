package org.goafabric.personservice.persistence.extensions;

import liquibase.integration.spring.MultiTenantSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import org.goafabric.personservice.crossfunctional.HttpInterceptor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Source: https://spring.io/blog/2022/07/31/how-to-integrate-hibernates-multitenant-feature-with-spring-data-jpa-in-a-spring-boot-application

@Component
@ImportRuntimeHints(TenantResolver.ApplicationRuntimeHints.class)

public class TenantResolver implements CurrentTenantIdentifierResolver, MultiTenantConnectionProvider, HibernatePropertiesCustomizer {

    private final DataSource dataSource;

    private final String schemaPrefix;

    private final String defaultSchema;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public TenantResolver(DataSource dataSource,
                          @Value("${multi-tenancy.default-schema:PUBLIC}") String defaultSchema,
                          @Value("${multi-tenancy.schema-prefix:_}") String schemaPrefix) {
        this.dataSource = dataSource;
        this.defaultSchema = defaultSchema;
        this.schemaPrefix = schemaPrefix;
    }

    /** Resolver for optional CompanyId via @TenantId Discriminator **/

    @Override
    public String resolveCurrentTenantIdentifier() {
        return HttpInterceptor.getCompanyId();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }

    /** Tenant Resolver for Schema **/

    @Override
    public Connection getConnection(String schema) throws SQLException {
        var connection = dataSource.getConnection();
        connection.setSchema(defaultSchema.equals(schema) ? defaultSchema : getSchemaName( HttpInterceptor.getTenantId()));
        return connection;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return getConnection(defaultSchema);
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }


    @Override
    public void releaseConnection(String s, Connection connection) throws SQLException {
        connection.setSchema(defaultSchema);
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }


    @Bean
    SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setShouldRun(false);
        return liquibase;
    }

    @Bean
    MultiTenantSpringLiquibase multiTenantSpringLiquibase(DataSource dataSource,
                                                          @Value("${multi-tenancy.migration.enabled}") Boolean enabled,
                                                          @Value("${multi-tenancy.schemas}") String schemas,
                                                          @Value("${spring.liquibase.change-log}") String changeLog) {

        var completeSchemas = new ArrayList<String>();
        Arrays.asList(schemas.split(",")).forEach(schema -> {
            completeSchemas.add(getSchemaName(schema));
            if (enabled) {
                new JdbcTemplate(dataSource).execute("CREATE SCHEMA IF NOT EXISTS " + getSchemaName(schema));
            }
        });

        var liquibase = new MultiTenantSpringLiquibase();
        liquibase.setChangeLog(changeLog);
        liquibase.setDataSource(dataSource);
        liquibase.setSchemas(completeSchemas);
        liquibase.setShouldRun(enabled);
        return liquibase;
    }

    private @Value("${spring.datasource.url}") String datasourceUrl;
    private String getSchemaName(String tenantId) {
        return datasourceUrl.contains("jdbc:h2") ? (schemaPrefix + tenantId).toUpperCase() : (schemaPrefix + tenantId);
    }

    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            //resources
            hints.reflection().registerType(java.util.ArrayList.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
            hints.reflection().registerType(ConcurrentHashMap.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        }
    }

}