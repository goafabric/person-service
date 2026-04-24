package org.goafabric.personservice.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Table("person")
public class PersonEo {

    @Id
    private String id;

    private String organizationId = "0";

    private String firstName;

    private String lastName;

    @MappedCollection(idColumn = "person_id", keyColumn = "id")
    private List<AddressEo> address;

    @Version //optimistic locking
    private Long version;


    public PersonEo(String id, String firstName, String lastName, List<AddressEo> address, Long version) {
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.version = version;
    }

    PersonEo() {}

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public List<AddressEo> getAddress() {
        return address;
    }


    public Long getVersion() {
        return version;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}
