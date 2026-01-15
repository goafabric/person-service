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
    var id: String?,

    var street: String,
    var city: String,

    @Version //optimistic locking
    var version: Long?
)

