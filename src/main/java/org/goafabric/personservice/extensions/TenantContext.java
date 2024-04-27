package org.goafabric.personservice.extensions;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public class TenantContext {
    private static final ThreadLocal<TenantContextRecord> CONTEXT =
            ThreadLocal.withInitial(() -> new TenantContextRecord("0", "0", "anonymous"));

    record TenantContextRecord(String tenantId, String organizationId, String userName) {
        public Map<String, String> toAdapterHeaderMap() {
            return Map.of("X-TenantId", getTenantId(), "X-OrganizationId", getOrganizationId(), "X-Auth-Request-Preferred-Username", getUserName());
        }
    }

    public static void setContext(HttpServletRequest request) {
        CONTEXT.set(new TenantContextRecord(
                getDefaultValue(request.getHeader("X-TenantId"), CONTEXT.get().tenantId),
                getDefaultValue(request.getHeader("X-OrganizationId"), CONTEXT.get().organizationId),
                getDefaultValue(request.getHeader("X-Auth-Request-Preferred-Username"), CONTEXT.get().userName))
        );
    }

    static void setContext(TenantContextRecord tenantContextRecord) {
        CONTEXT.set(tenantContextRecord);
    }

    public static void removeContext() {
        CONTEXT.remove();
    }

    private static String getDefaultValue(String value, String defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static String getTenantId() {
        return CONTEXT.get().tenantId();
    }

    public static String getOrganizationId() {
        return CONTEXT.get().organizationId();
    }

    public static String getUserName() {
        return CONTEXT.get().userName();
    }

    public static Map<String, String> getAdapterHeaderMap() {
        return CONTEXT.get().toAdapterHeaderMap();
    }


    public static void setTenantId(String tenant) {
        CONTEXT.set(new TenantContextRecord(tenant, CONTEXT.get().organizationId, CONTEXT.get().userName));
    }


}
