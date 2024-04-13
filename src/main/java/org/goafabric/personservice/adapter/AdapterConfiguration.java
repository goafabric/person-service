package org.goafabric.personservice.adapter;

import org.goafabric.personservice.extensions.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Base64;

@Configuration
public class AdapterConfiguration {

    @Bean
    public CalleeServiceAdapter calleeServiceAdapter(RestClient.Builder builder,
            @Value("${adapter.calleeservice.url}") String url, @Value("${adapter.timeout}") Long timeout, @Value("${adapter.calleeservice.authentication}") String authentication) {
        return createAdapter(CalleeServiceAdapter.class, builder, url, timeout, new String(Base64.getDecoder().decode(authentication)));
    }

    public static <A> A createAdapter(Class<A> adapterType, RestClient.Builder builder, String url, Long timeout, String authentication) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout.intValue());
        requestFactory.setReadTimeout(timeout.intValue());
        builder.baseUrl(url)
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBasicAuth(authentication, authentication);
                    TenantContext.getAdapterHeaderMap().forEach((key, value) -> request.getHeaders().set(key, value));
                    return execution.execute(request, body);
                })
                .requestFactory(requestFactory);
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build())).build()
                .createClient(adapterType);
    }

}


