package org.goafabric.personservice.persistence.multitenancy;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.MappedSuperclass;
import org.goafabric.personservice.crossfunctional.HttpInterceptor;

@MappedSuperclass
//@EntityListeners(AuditJpaListener.class)
public abstract class TenantAware {
    public static final String TENANT_FILTER = "TENANT_FILTER";

    @Access(AccessType.PROPERTY)
    public String getTenantId() {return HttpInterceptor.getTenantId();}
    
    public void setTenantId(String tenantId) {}

    public abstract String getId();
}
