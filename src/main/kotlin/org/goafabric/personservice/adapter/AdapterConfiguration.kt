package org.goafabric.personservice.adapter

import org.goafabric.personservice.extensions.HttpInterceptor
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
@ImportRuntimeHints(AdapterConfiguration.AdapterRuntimeHints::class)
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
            .requestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
                request.headers.setBasicAuth("admin", "admin")
                request.headers.add("X-TenantId", HttpInterceptor.getTenantId())
                request.headers.add("X-OrganizationId", HttpInterceptor.getTenantId())
                //TenantContext.getMap().forEach { key, value -> request.headers[key] = value }
                execution.execute(request, body)
            }
            .requestFactory(requestFactory)
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build())).build()
            .createClient(adapterType!!)
    }

    internal class AdapterRuntimeHints : RuntimeHintsRegistrar {
        override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
            hints.reflection().registerType(io.github.resilience4j.spring6.circuitbreaker.configure.CircuitBreakerAspect::class.java,  MemberCategory.INVOKE_DECLARED_METHODS)
        }
    }
}
