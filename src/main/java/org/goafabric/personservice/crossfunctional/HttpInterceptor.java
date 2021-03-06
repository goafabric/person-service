package org.goafabric.personservice.crossfunctional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@Slf4j
public class HttpInterceptor implements WebMvcConfigurer {
    private static final ThreadLocal<String> tenantId = new ThreadLocal<>();
    private static final ThreadLocal<String> userName = new ThreadLocal<>();

    public static String getTenantId() { return tenantId.get(); }
    public static String getUserName() { return userName.get(); }
    public static void   setTenantId(String tenantId) { HttpInterceptor.tenantId.set(tenantId); }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                tenantId.set(request.getHeader("X-TenantId") != null ? request.getHeader("X-TenantId") : "0"); //TODO
                userName.set(request.getHeader("X-Auth-Request-Preferred-Username") != null ? request.getHeader("X-Auth-Request-Preferred-Username")
                                                :  SecurityContextHolder.getContext().getAuthentication().getName());
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                tenantId.remove();
                userName.remove();
            }
        });
    }
}