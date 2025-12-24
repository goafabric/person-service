package org.goafabric.personservice.persistence.entity;

import jakarta.persistence.*;
import org.goafabric.personservice.persistence.extensions.AuditTrailListener;
import org.goafabric.personservice.persistence.extensions.KafkaPublisher;


@Entity
@Table(name="address")
@EntityListeners({AuditTrailListener.class, KafkaPublisher.class})
public class AddressEo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String street;
    private String city;

    @Version //optimistic locking
    private Long version;

    public AddressEo(String id, String street, String city, Long version) {
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
