package org.goafabric.personservice.persistence.extensions

import org.flywaydb.core.Flyway
import org.goafabric.personservice.extensions.HttpInterceptor
import org.goafabric.personservice.repository.extensions.DemoDataImporter
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.function.Consumer
import javax.sql.DataSource
import kotlin.collections.MutableMap
import kotlin.collections.set

// Source: https://spring.io/blog/2022/07/31/how-to-integrate-hibernates-multitenant-feature-with-spring-data-jpa-in-a-spring-boot-application
@Component
@RegisterReflectionForBinding(org.hibernate.binder.internal.TenantIdBinder::class, org.hibernate.generator.internal.TenantIdGeneration::class)
@ConditionalOnExpression("#{!('\${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
class TenantResolver(
    private val dataSource: DataSource,
    @param:Value("\${multi-tenancy.default-schema:PUBLIC}") private val defaultSchema: String,
    @param:Value("\${multi-tenancy.schema-prefix:_}") private val schemaPrefix: String
) : CurrentTenantIdentifierResolver, MultiTenantConnectionProvider, HibernatePropertiesCustomizer {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Resolver for optional CompanyId via @TenantId Discriminator  */
    override fun resolveCurrentTenantIdentifier(): String {
        return getOrgunitId()
    }

    companion object {
        fun getOrgunitId(): String {
            return "1";
        }
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
            if (defaultSchema == schema) defaultSchema else schemaPrefix + HttpInterceptor.getTenantId()
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

    override fun isUnwrappableAs(unwrapType: Class<*>): Boolean {
        return false
    }


    override fun <T> unwrap(unwrapType: Class<T>): T? {
        return null
    }

    /** Flyway configuration to create database schemas  */
    @Bean
    fun flywayMigrationStrategy(): FlywayMigrationStrategy {
        return FlywayMigrationStrategy { _: Flyway? -> }
    }

    @Bean
    fun schemaCreator(
        flyway: Flyway,
        @Value("\${database.provisioning.goals}") goals: String,
        @Value("\${multi-tenancy.tenants}") tenants: String,
        @Value("\${multi-tenancy.schema-prefix:_}") schemaPrefix: String,
        context: ApplicationContext?
    ): CommandLineRunner {
        return object : CommandLineRunner {
            override fun run(vararg args: String) {
                if (goals.contains("-migrate")) {
                    listOf(*tenants.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()).forEach(
                        Consumer { tenant: String ->
                            Flyway.configure().configuration(flyway.configuration)
                                .schemas(schemaPrefix + tenant).defaultSchema(schemaPrefix + tenant)
                                .placeholders(mapOf("tenantId" to tenant))
                                .load().migrate()
                        }
                    )
                }
                if (goals.contains("-terminate") && !goals.contains("-import")) {
                    SpringApplication.exit(context, ExitCodeGenerator { 0 })
                }
                if (args.size == 0 || "-check-integrity" != args.get(0)) {
                    context!!.getBean(DemoDataImporter::class.java).run()
                }
            }
        }
    }
}