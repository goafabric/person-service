package org.goafabric.personservice.persistence.extensions

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.*
import org.goafabric.personservice.extensions.HttpInterceptor
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.sql.DataSource

class AuditListener : ApplicationContextAware {
    @MappedSuperclass
    @EntityListeners(AuditListener::class)
    abstract class AuditAware {
        abstract fun getMyId() : String
    }

    private val log = LoggerFactory.getLogger(this.javaClass)

    enum class DbOperation {
        CREATE, READ, UPDATE, DELETE
    }

    internal data class AuditEvent(
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

    @PostLoad
    fun afterRead(`object`: Any) {
        insertAudit(DbOperation.READ, (`object` as AuditAware).getMyId(), `object`, `object`)
    }

    @PostPersist
    fun afterCreate(`object`: Any) {
        insertAudit(DbOperation.CREATE, (`object` as AuditAware).getMyId(), null, `object`)
    }

    @PostUpdate
    fun afterUpdate(`object`: Any) {
        val id: String = (`object` as AuditAware).getMyId()
        insertAudit(
            DbOperation.UPDATE, id,
            context!!.getBean(
                AuditJpaUpdater::class.java
            ).findOldObject(`object`.javaClass, id), `object`
        )
    }

    @PostRemove
    fun afterDelete(`object`: Any) {
        insertAudit(DbOperation.DELETE, (`object` as AuditAware).getMyId(), `object`, null)
    }

    private fun insertAudit(operation: DbOperation, referenceId: String, oldObject: Any?, newObject: Any?) {
        try {
            val auditEvent = createAuditEvent(operation, referenceId, oldObject, newObject)
            log.debug("New audit event :\n{}", auditEvent)
            context!!.getBean(
                AuditJpaInserter::class.java
            ).insertAudit(auditEvent, (oldObject ?: newObject)!!)
        } catch (e: Exception) {
            log.error("Error during audit:\n{}", e.message, e)
        }
    }

    @Throws(JsonProcessingException::class)
    private fun createAuditEvent(
        dbOperation: DbOperation, referenceId: String, oldObject: Any?, newObject: Any?
    ): AuditEvent {
        val date = Date(System.currentTimeMillis())
        return AuditEvent(
            UUID.randomUUID().toString(),
            HttpInterceptor.getTenantId(),
            referenceId,
            newObject!!.javaClass.simpleName,
            dbOperation,
            if (dbOperation == DbOperation.CREATE) HttpInterceptor.getUserName() else null,
            if (dbOperation == DbOperation.CREATE) date else null,
            if (dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) HttpInterceptor.getUserName() else null,
            if (dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) date else null,
            oldObject?.let { getJsonValue(it) },
            if (newObject == null) null else getJsonValue(newObject)
        )
    }

    @Throws(JsonProcessingException::class)
    private fun getJsonValue(`object`: Any): String {
        return ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(`object`)
    }

    @Component
    internal class AuditJpaUpdater {
        @PersistenceContext
        private val entityManager: EntityManager? = null

        @Transactional(propagation = Propagation.REQUIRES_NEW) //new transaction helps us to retrieve the old value still inside the db
        fun <T> findOldObject(clazz: Class<T>?, id: String?): T {
            return entityManager!!.find(clazz, id)
        }
    }

    @Component
    @RegisterReflectionForBinding(AuditEvent::class)
    internal class AuditJpaInserter(private val dataSource: DataSource,
                                    @param:Value("\${multi-tenancy.schema-prefix:_}") private val schemaPrefix: String) {
        fun insertAudit(auditEvent: AuditEvent?, `object`: Any) {
            SimpleJdbcInsert(dataSource)
                .withTableName(getTableName(`object`) + "_audit")
                .withSchemaName(schemaPrefix + HttpInterceptor.getTenantId())
                .execute(BeanPropertySqlParameterSource(auditEvent!!))
        }

        private fun getTableName(`object`: Any): String {
            return `object`.javaClass.simpleName.replace("Eo".toRegex(), "").lowercase(Locale.getDefault())
        }
    }

    companion object {
        private var context: ApplicationContext? = null
    }
}