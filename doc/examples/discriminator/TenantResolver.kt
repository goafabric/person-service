package org.goafabric.personservice.persistence.extensions

import org.flywaydb.core.Flyway
import org.goafabric.personservice.extensions.UserContext
import org.goafabric.personservice.extensions.UserContext.tenantId
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.sql.Connection
import java.sql.SQLException
import java.util.function.Consumer
import javax.sql.DataSource

@Component
@ConditionalOnExpression("#{!('\${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
class TenantResolver(
    private val dataSource: DataSource
) : CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {
    private val log = LoggerFactory.getLogger(this.javaClass)

    /** Resolver for optional CompanyId via @TenantId Discriminator  */
    override fun resolveCurrentTenantIdentifier(): String {
        return UserContext.tenantId;
    }

    override fun validateExistingCurrentSessions(): Boolean {
        return false
    }

    override fun customize(hibernateProperties: MutableMap<String, Any>) {
        hibernateProperties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] = this
    }

    /** Flyway configuration to create database schemas  */
    @Bean
    fun flywayMigrationStrategy(): FlywayMigrationStrategy {
        return FlywayMigrationStrategy { _: Flyway? -> }
    }

    @Bean
    fun schemaCreator(
        flyway: Flyway,
        @Value("\${database.provisioning.goals}") goals: String
    ): Boolean {
        if (goals.contains("-migrate")) {
            Flyway.configure().configuration(flyway.configuration)
                .load().migrate()
        }
        return true
    }
}