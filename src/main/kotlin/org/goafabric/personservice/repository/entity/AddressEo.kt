package org.goafabric.personservice.persistence.domain

import org.hibernate.annotations.GenericGenerator
import jakarta.persistence.*
import org.goafabric.personservice.persistence.extensions.AuditListener
import org.hibernate.annotations.TenantId

@Entity
@Table(name = "address")
class AddressEo (
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    val id: String? = null,

    @TenantId
    var companyId: String? = null,

    val street: String,
    val city: String,

    @Version //optimistic locking
    val version: Long? = null
) : AuditListener.AuditAware()  {
    override fun getMyId(): String {
        return id ?: ""
    }
}
