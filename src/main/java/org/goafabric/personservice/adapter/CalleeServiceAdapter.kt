package org.goafabric.personservice.adapter

import org.goafabric.personservice.persistence.multitenancy.TenantInspector
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@RegisterReflectionForBinding(Callee::class)
class CalleeServiceAdapter (
    private val restTemplate: RestTemplate) {
    
    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${adapter.calleeservice.url}")
    private val url: String? = null

    @Autowired
    private val cbFactory: CircuitBreakerFactory<*, *>? = null
    fun sayMyName(name: String?): Callee? {
        log.info("Calling CalleService ...")
        val callee = cbFactory!!.create(this.javaClass.simpleName).run(
            { restTemplate.getForObject("$url/callees/sayMyName?name={name}", Callee::class.java, name) }
        ) { throwable: Throwable -> throw NoFallbackAvailableException(throwable.message, throwable) }
        log.info("got: $callee")
        return callee
    }
}