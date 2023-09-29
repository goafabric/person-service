package org.goafabric.personservice.repository.extensions

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import jakarta.persistence.*
import org.goafabric.personservice.extensions.HttpInterceptor
import org.goafabric.personservice.persistence.extensions.TenantResolver
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.sql.DataSource

// Simple Audittrail that fulfills the requirements of logging content changes + user + aot support, could be db independant
class AuditTrailListener : ApplicationContextAware {
    private val log = LoggerFactory.getLogger(this.javaClass)

    enum class DbOperation {
        CREATE, READ, UPDATE, DELETE
    }

    internal data class AuditTrail(
        val id: String,
        val companyId: String,
        val referenceId: String,
        val type: String,
        val operation: DbOperation,
        val createdBy: String?,
        val createdAt: Date?,
        val modifiedBy: String?,
        val modifiedAt: Date?,
        val oldValue: String?,
        val newValue: String?
    )

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    @PostPersist
    fun afterCreate(`object`: Any) {
        insertAudit(DbOperation.CREATE, getId(`object`), null, `object`)
    }

    @PostUpdate
    fun afterUpdate(`object`: Any) {
        val id = getId(`object`)
        insertAudit(
            DbOperation.UPDATE, id,
            context!!.getBean(
                AuditJpaUpdater::class.java
            ).findOldObject(`object`.javaClass, id), `object`
        )
    }

    @PostRemove
    fun afterDelete(`object`: Any) {
        insertAudit(DbOperation.DELETE, getId(`object`), `object`, null)
    }

    private fun insertAudit(operation: DbOperation, referenceId: String, oldObject: Any?, newObject: Any?) {
        try {
            val auditTrail = createAuditTrail(operation, referenceId, oldObject, newObject)
            log.debug("New audit:\n{}", auditTrail)
            context!!.getBean(
                AuditJpaInserter::class.java
            ).insertAudit(auditTrail, oldObject ?: newObject)
        } catch (e: Exception) {
            log.error("Error during audit:\n{}", e.message, e)
        }
    }

    @Throws(JsonProcessingException::class)
    private fun createAuditTrail(
        dbOperation: DbOperation, referenceId: String, oldObject: Any?, newObject: Any?
    ): AuditTrail {
        val date = Date(System.currentTimeMillis())
        return AuditTrail(
            UUID.randomUUID().toString(),
            TenantResolver.getOrgunitId(),
            getTableName((newObject ?: oldObject)!!),
            referenceId,
            dbOperation,
            if (dbOperation == DbOperation.CREATE) HttpInterceptor.getUserName() else null,
            if (dbOperation == DbOperation.CREATE) date else null,
            if (dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) HttpInterceptor.getUserName() else null,
            if (dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) date else null,
            oldObject?.let { getJsonValue(it) },
            newObject?.let { getJsonValue(it) }
        )
    }

    @Throws(JsonProcessingException::class)
    private fun getJsonValue(`object`: Any): String {
        return ObjectMapper().registerModule(JavaTimeModule()).writerWithDefaultPrettyPrinter()
            .writeValueAsString(`object`)
    }

    @Component
    @ConditionalOnExpression("#{!('\${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
    internal class AuditJpaUpdater {
        @PersistenceContext
        private val entityManager: EntityManager? = null

        @Transactional(propagation = Propagation.REQUIRES_NEW) //new transaction helps us to retrieve the old value still inside the db
        fun <T> findOldObject(clazz: Class<T>?, id: String?): T {
            return entityManager!!.find(clazz, id)
        }
    }

    @Component
    @ConditionalOnExpression("#{!('\${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
    @RegisterReflectionForBinding(
        AuditTrail::class
    )
    internal class AuditJpaInserter(
        private val dataSource: DataSource, @param:Value(
            "\${multi-tenancy.schema-prefix:_}"
        ) private val schemaPrefix: String
    ) {
        fun insertAudit(auditTrail: AuditTrail?, `object`: Any?) { //we cannot use jpa because of the dynamic table name
            SimpleJdbcInsert(dataSource)
                .withSchemaName(schemaPrefix + HttpInterceptor.getTenantId())
                .withTableName("audit_trail")
                .execute(BeanPropertySqlParameterSource(auditTrail!!))
        }
    }

    companion object {
        private var context: ApplicationContext? = null
        private fun getId(`object`: Any): String {
            return context!!.getBean(EntityManagerFactory::class.java).persistenceUnitUtil.getIdentifier(`object`)
                .toString()
        }

        private fun getTableName(`object`: Any): String {
            return `object`.javaClass.getSimpleName().replace("Eo".toRegex(), "").lowercase(Locale.getDefault())
        }
    }
}
