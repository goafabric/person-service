package org.goafabric.personservice.persistence.entity

import jakarta.persistence.*
import org.goafabric.personservice.persistence.extensions.AuditTrailListener

@Entity
@Table(name = "address")
@EntityListeners(AuditTrailListener::class)
class AddressEo (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,

    val street: String,
    val city: String,

    @Version //optimistic locking
    val version: Long?
)

