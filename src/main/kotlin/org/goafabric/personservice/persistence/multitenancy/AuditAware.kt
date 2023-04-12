package org.goafabric.personservice.persistence.multitenancy

import org.goafabric.personservice.crossfunctional.HttpInterceptor
import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditListener::class)
abstract class AuditAware {
    abstract fun getMyId() : String
}