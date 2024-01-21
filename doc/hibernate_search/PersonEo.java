package org.goafabric.personservice.sync;

import jakarta.persistence.*;
import org.goafabric.personservice.repository.entity.AddressEo;
import org.goafabric.personservice.repository.extensions.AuditTrailListener;
import org.hibernate.annotations.TenantId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import java.util.List;

/*
    gradle:
    implementation("org.hibernate.search:hibernate-search-mapper-orm:7.0.0.Final")
    implementation("org.hibernate.search:hibernate-search-backend-elasticsearch:7.0.0.Final")
 */
@Entity
@Table(name = "person")
@EntityListeners({AuditTrailListener.class})
//@Document("#{@httpInterceptor.getPrefix()}person")
@Indexed(index = "core_0_person_names")
public class PersonEo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericField
    private String id;

    @TenantId
    private String orgunitId;

    @KeywordField
    private String firstName;

    @KeywordField
    private String lastName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id")
    private List<AddressEo> address;

    @Version //optimistic locking
    private Long version;


    public PersonEo(String id, String orgunitId, String firstName, String lastName, List<AddressEo> address, Long version) {
        this.id = id;
        this.orgunitId = orgunitId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.version = version;
    }

    private PersonEo() {}

    public String getId() {
        return id;
    }

    public String getOrgunitId() {
        return orgunitId;
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
