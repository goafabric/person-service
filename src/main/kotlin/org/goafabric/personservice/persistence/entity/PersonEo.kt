package org.goafabric.personservice.persistence.entity

import jakarta.persistence.*
import org.goafabric.personservice.persistence.extensions.AuditTrailListener
import org.goafabric.personservice.persistence.extensions.KafkaPublisher
import org.hibernate.annotations.TenantId
import org.springframework.data.mongodb.core.mapping.Document


@Entity
@Table(name = "person")
@Document("#{@httpInterceptor.getPrefix()}person")
@EntityListeners(AuditTrailListener::class, KafkaPublisher::class)
class PersonEo (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String?,

    @TenantId
    var organizationId: String?,

    val firstName: String?,
    val lastName: String?,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "person_id")
    val address: List<AddressEo>?,

    @Version //optimistic locking
    val version: Long?
)
