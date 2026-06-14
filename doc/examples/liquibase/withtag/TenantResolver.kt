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
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.function.Consumer
import javax.sql.DataSource


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
        @Value("\${spring.liquibase.change-log}") changeLog: String?,
        buildProperties: Optional<BuildProperties>
    ): MultiTenantSpringLiquibase {
        val appVersion = buildProperties.map { it.version }.orElse("unknown")
        val completeSchemas = ArrayList<String?>()
        val migrate = goals.contains("-migrate")
        val rollback = goals.contains("-rollback")

        listOf(*tenants.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            .forEach(Consumer { schema: String? ->
                completeSchemas.add(getSchemaName(schema))
                if (migrate) {
                    JdbcTemplate(dataSource).execute("CREATE SCHEMA IF NOT EXISTS " + getSchemaName(schema))
                }
            })

        if (rollback) {
            completeSchemas.forEach { schemaName ->
                val previousTag = findPreviousTag(dataSource, schemaName)
                if (previousTag != null) {
                    executeWithLiquibase(dataSource, changeLog, schemaName) { lb ->
                        log.info("Rolling back schema {} to tag {}", schemaName, previousTag)
                        lb.rollback(previousTag, null as String?, Contexts(), LabelExpression())
                    }
                } else {
                    log.warn("No previous tag found for schema {}, skipping rollback", schemaName)
                }
            }
            return MultiTenantSpringLiquibase()
        }


        if (migrate) {
            completeSchemas.forEach { schemaName ->
                executeWithLiquibase(dataSource, changeLog, schemaName) { lb ->
                    lb.update(Contexts(), LabelExpression())
                    log.info("Tagging schema {} with version {}", schemaName, appVersion)
                    lb.tag(appVersion)
                }
            }
        }


        val liquibase = MultiTenantSpringLiquibase()
        liquibase.changeLog = changeLog
        liquibase.dataSource = dataSource
        liquibase.schemas = completeSchemas
        liquibase.isShouldRun = migrate
        return liquibase
    }

    private fun findPreviousTag(dataSource: DataSource, schemaName: String?): String? {
        return try {
            JdbcTemplate(dataSource).queryForList(
                "SELECT TAG FROM $schemaName.DATABASECHANGELOG WHERE TAG IS NOT NULL ORDER BY ORDEREXECUTED DESC LIMIT 1 OFFSET 1",
                String::class.java
            ).firstOrNull()
        } catch (e: Exception) {
            log.warn("Could not query previous tag for schema {}: {}", schemaName, e.message)
            null
        }
    }

    private fun executeWithLiquibase(dataSource: DataSource, changeLog: String?, schemaName: String?, action: (Liquibase) -> Unit) {
        val conn = dataSource.connection
        conn.schema = schemaName
        val database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(conn))
        database.defaultSchemaName = schemaName
        Liquibase(changeLog?.removePrefix("classpath:"), ClassLoaderResourceAccessor(), database).use(action)
    }

    @Value("\${spring.datasource.url}")
    private val datasourceUrl: String? = null
    private fun getSchemaName(tenantId: String?): String {
        return if (datasourceUrl!!.contains("jdbc:h2")) (schemaPrefix + tenantId).uppercase(Locale.getDefault()) else (schemaPrefix + tenantId)
    }
}