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
class PersonEo (
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    val id: String? = "",

    @TenantId
    var companyId: String? = null,

    val firstName: String,
    val lastName: String,

    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    val address: AddressEo,

    @Version //optimistic locking
    val version: Long? = null
)
