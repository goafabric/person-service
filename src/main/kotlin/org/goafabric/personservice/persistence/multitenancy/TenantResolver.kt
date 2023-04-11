package org.goafabric.personservice.persistence.extensions

import org.flywaydb.core.Flyway
import org.goafabric.personservice.crossfunctional.HttpInterceptor
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.Map
import java.util.function.Consumer
import javax.sql.DataSource
import kotlin.collections.MutableMap
import kotlin.collections.set

// Source: https://spring.io/blog/2022/07/31/how-to-integrate-hibernates-multitenant-feature-with-spring-data-jpa-in-a-spring-boot-application
@Component
class TenantResolver(
    private val dataSource: DataSource,
    @param:Value("\${multi-tenancy.default-schema:PUBLIC}") private val defaultSchema: String,
    @param:Value("\${multi-tenancy.schema-prefix:_}") private val schema_prefix: String
) : CurrentTenantIdentifierResolver, MultiTenantConnectionProvider, HibernatePropertiesCustomizer {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Resolver for optional CompanyId via @TenantId Discriminator  */
    override fun resolveCurrentTenantIdentifier(): String {
        return HttpInterceptor.getCompanyId()
    }

    override fun validateExistingCurrentSessions(): Boolean {
        return false
    }

    override fun customize(hibernateProperties: MutableMap<String, Any>) {
        hibernateProperties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] = this
        hibernateProperties[AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER] = this
    }

    /** Tenant Resolver for Schema  */
    @Throws(SQLException::class)
    override fun getConnection(schema: String): Connection {
        val connection = dataSource.connection
        connection.schema =
            if (defaultSchema == schema) defaultSchema else schema_prefix + HttpInterceptor.getTenantId()
        log.info("## setting schema: " + connection.schema)
        return connection
    }

    @Throws(SQLException::class)
    override fun getAnyConnection(): Connection {
        return getConnection(defaultSchema)
    }

    @Throws(SQLException::class)
    override fun releaseAnyConnection(connection: Connection) {
        connection.close()
    }

    @Throws(SQLException::class)
    override fun releaseConnection(s: String, connection: Connection) {
        connection.schema = defaultSchema
        connection.close()
    }

    override fun supportsAggressiveRelease(): Boolean {
        return false
    }

    override fun isUnwrappableAs(unwrapType: Class<*>?): Boolean {
        return false
    }

    override fun <T> unwrap(unwrapType: Class<T>): T? {
        return null
    }

    /** Flyway configuration to create database schemas  */
    @Bean
    fun flywayMigrationStrategy(): FlywayMigrationStrategy {
        return FlywayMigrationStrategy { flyway: Flyway? -> }
    }

    @Bean
    fun schemas(
        flyway: Flyway,
        @Value("\${multi-tenancy.migration.enabled}") enabled: Boolean,
        @Value("\${multi-tenancy.schemas}") schemas: Array<String?>
    ): CommandLineRunner {
        return CommandLineRunner { args: Array<String> ->
            if (enabled) {
                listOf("0", "5a2f").forEach {schema ->
                    Flyway.configure()
                        .configuration(flyway.configuration)
                        .schemas(schema_prefix + schema)
                        .defaultSchema(schema_prefix + schema)
                        //.placeholders(Map.of("tenantId", it))
                        .load()
                        .migrate()
                }
            }
        }
    }
}