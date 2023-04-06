package org.goafabric.personservice.persistence.multitenancy;

import org.flywaydb.core.Flyway;
import org.goafabric.personservice.crossfunctional.HttpInterceptor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

@Component
class TenantSchemaResolver implements MultiTenantConnectionProvider, CurrentTenantIdentifierResolver, HibernatePropertiesCustomizer {

    @Autowired
    private DataSource dataSource;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @Override
    public String resolveCurrentTenantIdentifier() {
        return "TENANT_" + HttpInterceptor.getTenantId().toUpperCase();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return getConnection("PUBLIC");
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String schema) throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setSchema(schema);
        return connection;
    }

    @Override
    public void releaseConnection(String s, Connection connection) throws SQLException {
        connection.setSchema("PUBLIC");
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

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
        };
    }

    @Bean
    public CommandLineRunner schemas(Flyway flyway,
             @Value("${multi-tenancy.migration.enabled}") Boolean enabled, @Value("${multi-tenancy.schemas}") String schemas) {
        return args -> {
            if (enabled) {
                Arrays.asList(schemas.toUpperCase().split(",")).forEach(schema -> {
                            log.info("migrating schema: " + schema);
                            Flyway.configure()
                                    .configuration(flyway.getConfiguration())
                                    .schemas(schema)
                                    .defaultSchema(schema)
                                    .load()
                                    .migrate();
                        }
                );
            }
        };
    }

}