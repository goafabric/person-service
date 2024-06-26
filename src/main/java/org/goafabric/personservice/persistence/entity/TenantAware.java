package org.goafabric.personservice.persistence.entity;

import jakarta.persistence.MappedSuperclass;
import org.goafabric.personservice.extensions.TenantContext;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@MappedSuperclass
public class TenantAware {
    private String tenantId;

    public TenantAware() {
        this.tenantId = TenantContext.getTenantId();
    }

    public String getTenantId() {
        return tenantId;
    }
}
