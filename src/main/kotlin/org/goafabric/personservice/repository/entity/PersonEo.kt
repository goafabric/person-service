package org.goafabric.personservice.repository.entity

import jakarta.persistence.*
import org.goafabric.personservice.repository.extensions.AuditTrailListener
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.TenantId
import org.springframework.data.mongodb.core.mapping.Document


@Entity
@Table(name = "person")
@Document("#{@httpInterceptor.getPrefix()}person")
@EntityListeners(AuditTrailListener::class)
data class PersonEo (
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    val id: String? = "",

    @TenantId
    var orgunitId: String? = null,

    val firstName: String,
    val lastName: String,

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "person_id")
    val address: List<AddressEo>,

    @Version //optimistic locking
    val version: Long? = null
)
