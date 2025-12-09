package org.goafabric.personservice.persistence.extensions

import jakarta.persistence.*
import org.goafabric.personservice.extensions.UserContext
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.jacksonMapperBuilder
import java.util.*

// Simple Audittrail that fulfills the requirements of logging content changes + user + aot support, could be db independant
class AuditTrailListener : ApplicationContextAware {
    private val log = LoggerFactory.getLogger(this.javaClass)
    private val jsonMapper : JsonMapper = jacksonMapperBuilder().build()
    private var context: ApplicationContext? = null

    enum class DbOperation {
        CREATE, UPDATE, DELETE
    }

    @Entity
    @Table(name = "audit_trail")
    @Access(AccessType.FIELD)
    internal data class AuditTrail(
        @Id @GeneratedValue(strategy = GenerationType.UUID)
        val id: String?,
        val organizationId: String,
        val objectType: String,
        val objectId: String,
        @Enumerated(EnumType.STRING)
        val operation: DbOperation,
        val createdBy: String?,
        val createdAt: Date?,
        val modifiedBy: String?,
        val modifiedAt: Date?,
        val oldvalue: String?,
        val newvalue: String?
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
        insertAudit(DbOperation.UPDATE, id, context!!.getBean(AuditDao::class.java
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
            context?.getBean(AuditDao::class.java)?.insertAudit(auditTrail)
        } catch (e: Exception) {
            log.error("Error during audit:\n{}", e.message, e)
        }
    }

    private fun createAuditTrail(
        dbOperation: DbOperation, referenceId: String, oldObject: Any?, newObject: Any?
    ): AuditTrail {
        val date = Date(System.currentTimeMillis())
        return AuditTrail(
            id = null,
            organizationId = TenantResolver.getOrgunitId(),
            objectType = getTableName((newObject ?: oldObject)!!),
            objectId = referenceId,
            dbOperation,
            createdBy = if (dbOperation == DbOperation.CREATE) UserContext.userName else null,
            createdAt = if (dbOperation == DbOperation.CREATE) date else null,
            modifiedBy = if (dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) UserContext.userName else null,
            modifiedAt = if (dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) date else null,
            oldvalue = oldObject?.let { getJsonValue(it) },
            newvalue = newObject?.let { getJsonValue(it) }
        )
    }

    private fun getJsonValue(`object`: Any): String {
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(`object`)
    }

    @Component
    @ConditionalOnExpression("#{!('\${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
    internal class AuditDao {
        @PersistenceContext
        private val entityManager: EntityManager? = null

        @Transactional(propagation = Propagation.REQUIRES_NEW) @SuppressWarnings("kotlin:S6619") //new transaction helps us to retrieve the old value still inside the db
        fun <T> findOldObject(clazz: Class<T>?, id: String?): T {
            val e = entityManager?.find(clazz, id)
            val jsonMapper = jacksonMapperBuilder().build()
            return jsonMapper.readValue(jsonMapper.writeValueAsBytes(e), clazz)
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        fun insertAudit(auditTrail: AuditTrail) {
            entityManager?.persist(auditTrail)
        }
    }

    private fun getId(`object`: Any): String {
        return context!!.getBean(EntityManagerFactory::class.java).persistenceUnitUtil.getIdentifier(`object`)
            .toString()
    }

    private fun getTableName(`object`: Any): String {
        return `object`.javaClass.getSimpleName().replace("Eo".toRegex(), "").lowercase(Locale.getDefault())
    }
}
