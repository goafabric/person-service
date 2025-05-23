package org.goafabric.personservice.persistence.extensions

import org.flywaydb.core.Flyway
import org.flywaydb.core.internal.publishing.PublishingConfigurationExtension
import org.flywaydb.database.postgresql.TransactionalModel
import org.goafabric.personservice.extensions.UserContext
import org.goafabric.personservice.extensions.UserContext.tenantId
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.annotation.RegisterReflection
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.util.function.Consumer
import javax.sql.DataSource
import kotlin.collections.set

@Component
@ConditionalOnExpression("#{!('\${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
@RegisterReflection(
    classes = [PublishingConfigurationExtension::class, TransactionalModel::class],
    memberCategories = [MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS]
)
class TenantResolver(
    private val dataSource: DataSource,
    @param:Value("\${multi-tenancy.default-schema:PUBLIC}") private val defaultSchema: String,
    @param:Value("\${multi-tenancy.schema-prefix:_}") private val schemaPrefix: String
) : CurrentTenantIdentifierResolver<String>, MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Resolver for optional CompanyId via @TenantId Discriminator  */
    override fun resolveCurrentTenantIdentifier(): String {
        return getOrgunitId()
    }

    companion object {
        fun getOrgunitId(): String {
            return UserContext.organizationId;
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
            if (defaultSchema == schema) defaultSchema else schemaPrefix + UserContext.tenantId
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

    @RegisterReflectionForBinding(TenantResolver::class)
    fun getPrefix(): String {
        return schemaPrefix + tenantId + "_"
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
    ): Boolean {
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
        return true
    }
}