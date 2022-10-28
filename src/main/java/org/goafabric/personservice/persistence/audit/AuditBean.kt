package org.goafabric.personservice.persistence.audit

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.goafabric.personservice.crossfunctional.HttpInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
/** A class that audits all registered entities with @EntityListeners and writes the Audit Entries to the database  */
class AuditBean {
    private val log = LoggerFactory.getLogger(this::class.java)

    enum class DbOperation {
        CREATE, READ, UPDATE, DELETE
    }

    internal data class AuditEvent (
        val id: String,
        val tenantId: String?,
        val referenceId: String,
        val type: String,
        val operation: DbOperation,
        val createdBy: String?,
        val createdAt: Date?,
        val modifiedBy: String?,
        val modifiedAt: Date?,
        val oldValue: String?,
        val newValue: String?,
    )

    internal interface AuditInserter {
        fun insertAudit(auditEvent: AuditEvent?, `object`: Any)
    }

    @Autowired
    private val auditInserter: AuditInserter? = null
    fun afterRead(`object`: Any?, id: String) {
        insertAudit(DbOperation.READ, id, `object`, `object`)
    }

    fun afterCreate(`object`: Any?, id: String) {
        insertAudit(DbOperation.CREATE, id, null, `object`)
    }

    fun afterUpdate(`object`: Any?, id: String, oldObject: Any?) {
        insertAudit(DbOperation.UPDATE, id, oldObject, `object`)
    }

    fun afterDelete(`object`: Any?, id: String) {
        insertAudit(DbOperation.DELETE, id, `object`, null)
    }

    private fun insertAudit(operation: DbOperation, referenceId: String, oldObject: Any?, newObject: Any?) {
        try {
            val auditEvent = createAuditEvent(operation, referenceId, oldObject, newObject)
            log.debug("New audit event :\n{}", auditEvent)
            auditInserter!!.insertAudit(auditEvent, (oldObject ?: newObject)!!)
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
            id = UUID.randomUUID().toString(),
            referenceId = referenceId,
            tenantId = HttpInterceptor.getTenantId(),
            operation = dbOperation,
            type = newObject!!.javaClass.simpleName,
            createdBy = (if (dbOperation == DbOperation.CREATE) HttpInterceptor.getUserName() else null),
            createdAt = (if (dbOperation == DbOperation.CREATE) date else null),
            modifiedBy = (if (dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) HttpInterceptor.getUserName() else null),
            modifiedAt = (if (dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) date else null),
            oldValue = (oldObject?.let { getJsonValue(it) }),
            newValue = (if (newObject == null) null else getJsonValue(newObject)))
    }

    @Throws(JsonProcessingException::class)
    private fun getJsonValue(`object`: Any): String {
        return ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(`object`)
    }
}