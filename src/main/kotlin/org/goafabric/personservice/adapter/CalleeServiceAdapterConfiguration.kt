package org.goafabric.personservice.adapter

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
class CalleeServiceAdapterConfiguration {
    @Bean
    fun calleeServiceAdapter( //ReactorLoadBalancerExchangeFilterFunction lbFunction,
        @Value("\${adapter.calleeservice.url}") url: String,
        @Value("\${adapter.timeout}") timeout: Long,
        @Value("\${adapter.maxlifetime:-1}") maxLifeTime: Long
    ): CalleeServiceAdapter {
        return createAdapter(CalleeServiceAdapter::class.java, url, timeout, maxLifeTime)
    }

    companion object {
        fun <A> createAdapter(adapterType: Class<A>, url: String, timeout: Long, maxLifeTime: Long): A {
            val builder = WebClient.builder()
            builder.baseUrl(url)
                .defaultHeaders { header -> header.setBasicAuth("admin", "admin") }
                .clientConnector(
                    ReactorClientHttpConnector(
                        HttpClient.create(
                            ConnectionProvider.builder("custom").maxLifeTime(
                                Duration.ofMillis(
                                    maxLifeTime!!
                                )
                            ).build()
                        )
                    )
                )
            return HttpServiceProxyFactory.builder(WebClientAdapter.forClient(builder.build())).blockTimeout(
                Duration.ofMillis(timeout!!)
            )
            .build().createClient<A>(adapterType)
        }
    }
}
