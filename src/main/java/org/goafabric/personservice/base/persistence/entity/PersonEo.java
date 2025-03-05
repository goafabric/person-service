package org.goafabric.personservice.base.persistence.entity;

import jakarta.persistence.*;
import org.goafabric.personservice.base.persistence.extensions.AuditTrailListener;
import org.hibernate.annotations.TenantId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Entity
@Table(name = "person")
@EntityListeners(AuditTrailListener.class)
@Document("#{@httpInterceptor.getTenantPrefix()}person") @org.springframework.data.elasticsearch.annotations.Document(indexName = "#{@httpInterceptor.getTenantPrefix()}person")
public class PersonEo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @TenantId
    private String organizationId;

    private String givenName;

    private String familyName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    private List<AddressEo> address;

    @Version //optimistic locking
    private Long version;


    public PersonEo(String id, String givenName, String familyName, String middleName, List<AddressEo> address, Long version) {
        this.id = id;
        this.givenName = givenName;
        this.familyName = familyName;
        this.address = address;
        this.version = version;
    }

    PersonEo() {}

    public String getId() {
        return id;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
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
