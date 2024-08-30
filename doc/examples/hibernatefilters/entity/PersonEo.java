package org.goafabric.personservice.persistence.entity;

import jakarta.persistence.*;
import org.goafabric.personservice.persistence.extensions.AuditTrailListener;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Entity
@Table(name = "person")
@EntityListeners(AuditTrailListener.class)
@Document("#{@httpInterceptor.getPrefix()}person")
public class PersonEo extends TenantAndOrganizationAware {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String firstName;

    private String lastName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    private List<AddressEo> address;

    @Version //optimistic locking
    private Long version;

    public PersonEo(String id, String firstName, String lastName, List<AddressEo> address, Long version) {
        super();
        this.id = id;
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

}
