package org.goafabric.personservice.persistence.entity;

import jakarta.persistence.*;
import org.goafabric.personservice.extensions.TenantContext;
import org.goafabric.personservice.persistence.extensions.AuditTrailListener;


@Entity
@Table(name="address")
@EntityListeners(AuditTrailListener.class)

//@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
//@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class AddressEo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String tenantId;

    private String street;
    private String city;

    @Version //optimistic locking
    private Long version;

    public AddressEo(String id, String street, String city, Long version) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.version = version;

        this.tenantId = TenantContext.getTenantId(); //set tenantId for save and update operations
    }

    private AddressEo() {
    }

    public String getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public Long getVersion() {
        return version;
    }
}
