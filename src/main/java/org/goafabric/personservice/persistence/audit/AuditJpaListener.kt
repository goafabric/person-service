package org.goafabric.personservice.persistence.audit

import org.goafabric.personservice.persistence.audit.AuditBean.AuditInserter
import org.goafabric.personservice.persistence.multitenancy.TenantAware
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import jakarta.persistence.*
import javax.sql.DataSource

class AuditJpaListener : ApplicationContextAware {
    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        context = applicationContext
    }

    @PostLoad
    fun afterRead(`object`: Any) {
        context!!.getBean(AuditBean::class.java).afterRead(`object`, (`object` as TenantAware).getMyId())
    }

    @PostPersist
    fun afterCreate(`object`: Any) {
        context!!.getBean(AuditBean::class.java).afterCreate(`object`, (`object` as TenantAware).getMyId())
    }

    @PostUpdate
    fun afterUpdate(`object`: Any) {
        context!!.getBean(AuditBean::class.java).afterUpdate(
            `object`, (`object` as TenantAware).getMyId(),
            context!!.getBean(AuditJpaUpdater::class.java).findOldObject(`object`.javaClass, `object`.getMyId())
        )
    }

    @PostRemove
    fun afterDelete(`object`: Any) {
        context!!.getBean(AuditBean::class.java).afterDelete(`object`, (`object` as TenantAware).getMyId())
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
    internal class AuditJpaInserter : AuditInserter {
        @Autowired
        private val dataSource: DataSource? = null
        override fun insertAudit(
            auditEvent: AuditBean.AuditEvent?,
            `object`: Any
        ) { //we cannot use jpa because of the dynamic table name
            SimpleJdbcInsert(dataSource!!).withTableName(getTableName(`object`) + "_audit")
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