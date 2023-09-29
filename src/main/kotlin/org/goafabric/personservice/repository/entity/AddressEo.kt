package org.goafabric.personservice.repository.entity

import org.hibernate.annotations.GenericGenerator
import jakarta.persistence.*
import org.goafabric.personservice.repository.extensions.AuditTrailListener
import org.hibernate.annotations.TenantId

@Entity
@Table(name = "address")
@EntityListeners(AuditTrailListener::class)
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
)

