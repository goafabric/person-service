package org.goafabric.personservice.persistence.domain

import org.goafabric.personservice.persistence.multitenancy.AuditAware
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Where
import jakarta.persistence.*
import org.hibernate.annotations.TenantId

@Entity
@Table(name = "address")
class AddressBo (
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
) : AuditAware()  {
    override fun getMyId(): String {
        return id ?: ""
    }
}
