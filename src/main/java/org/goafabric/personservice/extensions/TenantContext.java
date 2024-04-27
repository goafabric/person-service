package org.goafabric.personservice.extensions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public class TenantContext {
    private static final ThreadLocal<TenantContextRecord> CONTEXT =
            ThreadLocal.withInitial(() -> new TenantContextRecord("0", "0", "anonymous"));

    record TenantContextRecord(String tenantId, String organizationId, String userName) {
        public Map<String, String> toAdapterHeaderMap() {
            return Map.of("X-TenantId", tenantId, "X-OrganizationId", organizationId, "X-Auth-Request-Preferred-Username", userName);
        }
    }

    public static void setContext(HttpServletRequest request) {
        setContext(request.getHeader("X-TenantId"), request.getHeader("X-OrganizationId"),
                request.getHeader("X-Auth-Request-Preferred-Username"), request.getHeader("X-UserInfo"));
    }

    static void setContext(String tenantId, String organizationId, String userName, String userInfo) {
        CONTEXT.set(new TenantContextRecord(
                getDefaultValue(tenantId, CONTEXT.get().tenantId),
                getDefaultValue(organizationId, CONTEXT.get().organizationId),
                getDefaultValue(getUserNameFromUserInfo(userInfo), getDefaultValue(userName, CONTEXT.get().userName))
        ));

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

    private static String getUserNameFromUserInfo(String userInfo) {
        return userInfo != null ? (String) decodeUserInfo(userInfo).get("preferred_username") : null;
    }

    private static Map<String, Object> decodeUserInfo(String userInfo) {
        try {
            return new ObjectMapper().readValue(Base64.getUrlDecoder().decode(userInfo), new TypeReference<>() {});
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    //eyJwcmVmZXJyZWRfdXNlcm5hbWUiOiJqb24gZG9lIiwiYWxnIjoiSFMyNTYifQ.e30.OsLaWah2xLrm4GOGbR0OdZ0BCtPC6wHcQ_ipyuHIAsY
}
