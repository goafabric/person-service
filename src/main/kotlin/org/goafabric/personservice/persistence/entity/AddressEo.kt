package org.goafabric.personservice.persistence.entity

import jakarta.persistence.*
import org.goafabric.personservice.persistence.extensions.AuditTrailListener
import org.goafabric.personservice.persistence.extensions.KafkaPublisher

@Entity
@Table(name = "address")
@EntityListeners(AuditTrailListener::class, KafkaPublisher::class)
class AddressEo (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,

    val street: String,
    val city: String,

    @Version //optimistic locking
    val version: Long?
)

