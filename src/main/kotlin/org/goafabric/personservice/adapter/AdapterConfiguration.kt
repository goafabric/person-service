package org.goafabric.personservice.adapter

import org.goafabric.personservice.extensions.HttpInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class AdapterConfiguration {
    @Bean
    fun calleeServiceAdapter( //ReactorLoadBalancerExchangeFilterFunction lbFunction,
        builder: RestClient.Builder,
        @Value("\${adapter.calleeservice.url}") url: String?,
        @Value("\${adapter.timeout}") timeout: Long,
        @Value("\${adapter.maxlifetime:-1}") maxLifeTime: Long?
    ): CalleeServiceAdapter {
        return createAdapter(CalleeServiceAdapter::class.java, builder, url, timeout, maxLifeTime)
    }

    companion object {
        fun <A> createAdapter(
            adapterType: Class<A>,
            builder: RestClient.Builder,
            url: String?,
            timeout: Long,
            maxLifeTime: Long?
        ): A {
            val requestFactory = SimpleClientHttpRequestFactory()
            requestFactory.setConnectTimeout(timeout.toInt())
            requestFactory.setReadTimeout(timeout.toInt())
            builder.baseUrl(url!!)
                .defaultHeaders { header: HttpHeaders -> header.setBasicAuth("admin", "admin") }
                .requestFactory(requestFactory)
            //.clientConnector(new ReactorClientHttpConnector(HttpClient.create(ConnectionProvider.builder("custom").maxLifeTime(Duration.ofMillis(maxLifeTime)).build())));
            return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build())).build()
                .createClient(adapterType)
        }
    }

    fun <A> createAdapter(
        adapterType: Class<A>?,
        builder: RestClient.Builder,
        url: String?,
        timeout: Long,
        maxLifeTime: Long?
    ): A {
        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setConnectTimeout(timeout.toInt())
        requestFactory.setReadTimeout(timeout.toInt())
        builder.baseUrl(url!!)
            .defaultHeaders { httpHeaders: HttpHeaders ->
                httpHeaders.setBasicAuth("admin", "admin") //for OIDC this would be the jwt
                httpHeaders.add("X-TenantId", HttpInterceptor.getTenantId())
                httpHeaders.add("X-OrganizationId", HttpInterceptor.getTenantId())
            }
            .requestFactory(requestFactory)
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build())).build()
            .createClient(adapterType!!)
    }
}