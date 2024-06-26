package org.goafabric.personservice.persistence.entity;

import jakarta.persistence.*;
import org.goafabric.personservice.persistence.extensions.AuditTrailListener;


@Entity
@Table(name="address")
@EntityListeners(AuditTrailListener.class)
public class AddressEo extends TenantAware {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String street;
    private String city;

    @Version //optimistic locking
    private Long version;

    public AddressEo(String id, String street, String city, Long version) {
        super();
        this.id = id;
        this.street = street;
        this.city = city;
        this.version = version;
    }

    AddressEo() {
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
