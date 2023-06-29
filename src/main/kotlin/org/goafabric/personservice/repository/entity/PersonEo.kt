package org.goafabric.personservice.persistence.domain

import jakarta.persistence.*
import org.goafabric.personservice.persistence.extensions.AuditListener
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.TenantId


@Entity
@Table(name = "person")
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
) : AuditListener.AuditAware() {
    override fun getMyId(): String {
        return id ?: ""
    }
}