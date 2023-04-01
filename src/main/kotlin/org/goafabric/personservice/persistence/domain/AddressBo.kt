package org.goafabric.personservice.persistence.domain

import org.goafabric.personservice.persistence.multitenancy.TenantAware
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Where
import jakarta.persistence.*

@Entity
@Table(name = "address")
@Where(clause = TenantAware.TENANT_FILTER)
class AddressBo (
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    val id: String? = null,
    val street: String,
    val city: String,

    @Version //optimistic locking
    val version: Long? = null
) : TenantAware()  {
    override fun getMyId(): String {
        return id ?: ""
    }
}
