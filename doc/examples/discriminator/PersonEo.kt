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
    var id: String?,

    @TenantId
    var organizationId: String?,

    var firstName: String?,
    var lastName: String?,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "person_id")
    var address: List<AddressEo>?,

    @Version //optimistic locking
    var version: Long?
)
