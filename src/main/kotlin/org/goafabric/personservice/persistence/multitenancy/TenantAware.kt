package org.goafabric.personservice.persistence.multitenancy

import org.goafabric.personservice.crossfunctional.HttpInterceptor
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditListener::class)
abstract class TenantAware {
    companion object {
        const val TENANT_FILTER = "TENANT_FILTER"
    }

    @Access(AccessType.PROPERTY)
    fun getTenantId(): String? { return HttpInterceptor.getTenantId() }

    fun setTenantId(tenantId: String?) {}

    abstract fun getMyId() : String
}