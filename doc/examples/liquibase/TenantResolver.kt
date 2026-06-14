package org.goafabric.personservice.persistence.extensions

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.integration.spring.MultiTenantSpringLiquibase
import liquibase.resource.ClassLoaderResourceAccessor
import org.goafabric.personservice.extensions.UserContext
import org.goafabric.personservice.extensions.UserContext.tenantId
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource


//implementation("org.liquibase:liquibase-core")

@Component
@ConditionalOnExpression("#{!('\${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
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
        try {
            connection.schema = if (defaultSchema == schema) defaultSchema else getSchemaName(UserContext.tenantId)
        } catch (e: Exception) {
            connection.close()
            throw e
        }
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

    @Bean
    fun multiTenantSpringLiquibase(
        dataSource: DataSource,
        @Value("\${database.provisioning.goals}") goals: String,
        @Value("\${multi-tenancy.tenants}") tenants: String,
        @Value("\${spring.liquibase.change-log:db/changelog/changelog-root.xml}") changeLog: String,
        @Value("\${database.provisioning.rollback-count:6}") rollbackCount: Int
    ): MultiTenantSpringLiquibase {
        val migration = goals.contains("-migrate")
        val rollback = goals.contains("-rollback")
        val completeSchemas = tenants.split(",").filter { it.isNotEmpty() }.map { getSchemaName(it) }

        if (migration) {
            completeSchemas.forEach { schema -> JdbcTemplate(dataSource).execute("CREATE SCHEMA IF NOT EXISTS $schema") }
        }

        if (rollback) {
            completeSchemas.forEach { schemaName ->
                log.info("Rolling back {} changeset(s) for schema {}", rollbackCount, schemaName)

                val conn = dataSource.connection
                conn.schema = schemaName
                val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(conn))
                database.defaultSchemaName = schemaName
                Liquibase(changeLog, ClassLoaderResourceAccessor(), database)
                    .use { lb -> lb.rollback(rollbackCount, null as String?, Contexts(), LabelExpression()) }

            }
        }

        val liquibase = MultiTenantSpringLiquibase()
        liquibase.changeLog = changeLog
        liquibase.dataSource = dataSource
        liquibase.schemas = completeSchemas
        liquibase.isShouldRun = migration

        return liquibase
    }

    @Value("\${spring.datasource.url}")
    private val datasourceUrl: String? = null
    private fun getSchemaName(tenantId: String?): String {
        return if (datasourceUrl!!.contains("jdbc:h2")) (schemaPrefix + tenantId).uppercase(Locale.getDefault()) else (schemaPrefix + tenantId)
    }
}