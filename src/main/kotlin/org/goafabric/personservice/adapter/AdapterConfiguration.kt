package org.goafabric.personservice.adapter

import org.goafabric.personservice.extensions.TenantContext
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class AdapterConfiguration {
    @Bean
    fun calleeServiceAdapter(
        builder: RestClient.Builder,
        @Value("\${adapter.calleeservice.url}") url: String?,
        @Value("\${adapter.timeout}") timeout: Long
    ): CalleeServiceAdapter {
        return createAdapter(CalleeServiceAdapter::class.java, builder, url, timeout)
    }

    fun <A> createAdapter(
        adapterType: Class<A>?,
        builder: RestClient.Builder,
        url: String?,
        timeout: Long
    ): A {
        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setConnectTimeout(timeout.toInt())
        requestFactory.setReadTimeout(timeout.toInt())
        builder.baseUrl(url!!)
            .requestInterceptor { request, body, execution ->
                TenantContext.adapterHeaderMap.forEach { (key, value) -> request.headers.add(key, value) }
                execution.execute(request, body)
            }
            .requestFactory(requestFactory)
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build())).build()
            .createClient(adapterType!!)
    }
}
