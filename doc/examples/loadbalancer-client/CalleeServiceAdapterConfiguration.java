package org.goafabric.personservice.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class CalleeServiceAdapterConfiguration {

    @Bean
    public CalleeServiceAdapter calleeServiceAdapter(ReactorLoadBalancerExchangeFilterFunction lbFunction,
                                                     @Value("${adapter.calleeservice.url}") String url, @Value("${adapter.timeout}") Long timeout, @Value("${adapter.maxlifetime:-1}") Long maxLifeTime) {
        return createAdapter(CalleeServiceAdapter.class, lbFunction, url, timeout, maxLifeTime);
    }

    public static <A> A createAdapter(Class<A> adapterType, ReactorLoadBalancerExchangeFilterFunction lbFunction, String url, Long timeout, Long maxLifeTime) {
        var builder = WebClient.builder();
        builder.baseUrl(url)
                .defaultHeaders(header -> header.setBasicAuth("admin", "admin"))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.builder("custom").maxLifeTime(Duration.ofMillis(maxLifeTime)).build())));
                if (url.contains("lb://")) { builder.filter(lbFunction); }

        return HttpServiceProxyFactory.builder(WebClientAdapter.forClient(builder.build())).blockTimeout(Duration.ofMillis(timeout))
                .build().createClient(adapterType);
    }
}


