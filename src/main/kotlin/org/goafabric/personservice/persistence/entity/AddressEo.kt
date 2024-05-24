package org.goafabric.personservice.persistence.entity

import jakarta.persistence.*
import org.goafabric.personservice.persistence.extensions.AuditTrailListener
import org.hibernate.annotations.GenericGenerator

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

