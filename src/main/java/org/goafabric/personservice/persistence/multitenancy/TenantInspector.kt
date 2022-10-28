package org.goafabric.personservice.persistence.multitenancy

import org.goafabric.personservice.crossfunctional.HttpInterceptor
import org.hibernate.resource.jdbc.spi.StatementInspector
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
@RegisterReflectionForBinding(TenantInspector::class)
class TenantInspector : StatementInspector {
    override fun inspect(sql: String): String {
        return if (sql.contains(TenantAware.TENANT_FILTER)) sql.replace(TenantAware.TENANT_FILTER, "tenant_id = '" + HttpInterceptor.getTenantId() + "'"
        ) else sql
    }

    @Bean
    fun hibernatePropertiesCustomizer(): HibernatePropertiesCustomizer {
        return HibernatePropertiesCustomizer { hibernateProperties: MutableMap<String?, Any?> ->
            hibernateProperties["hibernate.session_factory.statement_inspector"] = TenantInspector::class.java.name
        }
    }
}