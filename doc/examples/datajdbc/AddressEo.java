package org.goafabric.personservice.persistence.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name="address")
public class AddressEo {
    @Id
    private String id;

    private String street;
    private String city;

    @Version //optimistic locking
    private Long version;

    public AddressEo(String id, String street, String city, Long version) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
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
