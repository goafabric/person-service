package org.goafabric.personservice.persistence.entity;

import jakarta.persistence.*;
import org.goafabric.personservice.extensions.TenantContext;
import org.goafabric.personservice.persistence.extensions.AuditTrailListener;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Entity
@Table(name = "person")
@EntityListeners(AuditTrailListener.class)
@Document("#{@httpInterceptor.getPrefix()}person")
public class PersonEo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OrganizationId
    private String organizationId;

    private String firstName;

    private String lastName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    private List<AddressEo> address;

    @Version //optimistic locking
    private Long version;

    public PersonEo(String id, String firstName, String lastName, List<AddressEo> address, Long version) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.version = version;
        this.organizationId = TenantContext.getOrganizationId(); //set organizationId for save and update operations
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
