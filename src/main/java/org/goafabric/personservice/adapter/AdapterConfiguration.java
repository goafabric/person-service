package org.goafabric.personservice.adapter;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.goafabric.personservice.extensions.UserContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RegisterReflection(classes = org.springframework.web.client.ResourceAccessException.class, memberCategories = MemberCategory.INVOKE_DECLARED_METHODS)
public class AdapterConfiguration {

    @Bean
    public CalleeServiceAdapter calleeServiceAdapter(RestClient.Builder builder,
            @Value("${adapter.calleeservice.url}") String url, @Value("${adapter.timeout}") Long timeout) {
        return createAdapter(CalleeServiceAdapter.class, builder, url, timeout);
    }

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    public <A> A createAdapter(Class<A> adapterType, RestClient.Builder builder, String url, Long timeout) {
        var cb = circuitBreakerRegistry.circuitBreaker("calleeservice");

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout.intValue());
        requestFactory.setReadTimeout(timeout.intValue());
        builder.baseUrl(url)
                .requestInterceptor((request, body, execution) -> {
                    UserContext.getAdapterHeaderMap().forEach((key, value) -> request.getHeaders().set(key, value));
                    return execution.execute(request, body);
                })
                .requestInterceptor((request, body, execution) ->
                        {
                            try {
                                return cb.executeCallable(() -> execution.execute(request, body));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .requestFactory(requestFactory);
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build())).build()
                .createClient(adapterType);
    }

}


