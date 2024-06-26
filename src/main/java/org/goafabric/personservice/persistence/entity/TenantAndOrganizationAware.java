package org.goafabric.personservice.persistence.entity;

import jakarta.persistence.MappedSuperclass;
import org.goafabric.personservice.extensions.TenantContext;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@FilterDef(name = "tenantFilterOrg", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilterOrg", condition = "tenant_id = :tenantId")
@FilterDef(name = "organizationFilter", parameters = @ParamDef(name = "organizationId", type = String.class))
@Filter(name = "organizationFilter", condition = "organization_id = :organizationId")
@MappedSuperclass
public class TenantAndOrganizationAware {
    private String tenantId;
    private String organizationId;

    public TenantAndOrganizationAware() {
        this.tenantId = TenantContext.getTenantId();
        this.organizationId = TenantContext.getOrganizationId();
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

}
