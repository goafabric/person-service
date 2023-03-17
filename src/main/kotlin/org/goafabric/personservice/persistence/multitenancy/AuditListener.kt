package org.goafabric.personservice.persistence.multitenancy

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.*
import org.goafabric.personservice.crossfunctional.HttpInterceptor
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.BeansException
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
    private val log = LoggerFactory.getLogger(this.javaClass)

    enum class DbOperation {
        CREATE, READ, UPDATE, DELETE
    }

    internal data class AuditEvent(
        val id: String,
        val tenantId: String,
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
        insertAudit(DbOperation.READ, (`object` as TenantAware).getMyId(), `object`, `object`)
    }

    @PostPersist
    fun afterCreate(`object`: Any) {
        insertAudit(DbOperation.CREATE, (`object` as TenantAware).getMyId(), null, `object`)
    }

    @PostUpdate
    fun afterUpdate(`object`: Any) {
        val id: String = (`object` as TenantAware).getMyId()
        insertAudit(
            DbOperation.UPDATE, id,
            context!!.getBean(
                AuditJpaUpdater::class.java
            ).findOldObject(`object`.javaClass, id), `object`
        )
    }

    @PostRemove
    fun afterDelete(`object`: Any) {
        insertAudit(DbOperation.DELETE, (`object` as TenantAware).getMyId(), `object`, null)
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
    internal class AuditJpaInserter(private val dataSource: DataSource) {
        fun insertAudit(auditEvent: AuditEvent?, `object`: Any) { //we cannot use jpa because of the dynamic table name
            SimpleJdbcInsert(dataSource).withTableName(getTableName(`object`) + "_audit")
                .execute(BeanPropertySqlParameterSource(auditEvent!!))
        }

        private fun getTableName(`object`: Any): String {
            return `object`.javaClass.simpleName.replace("Bo".toRegex(), "").lowercase(Locale.getDefault())
        }
    }

    companion object {
        private var context: ApplicationContext? = null
    }
}