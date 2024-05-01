package org.goafabric.personservice.persistence.entity

import org.hibernate.annotations.GenericGenerator
import jakarta.persistence.*
import org.goafabric.personservice.persistence.extensions.AuditTrailListener

@Entity
@Table(name = "address")
@EntityListeners(AuditTrailListener::class)
class AddressEo (
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    val id: String? = null,

    val street: String,
    val city: String,

    @Version //optimistic locking
    val version: Long? = null
)

