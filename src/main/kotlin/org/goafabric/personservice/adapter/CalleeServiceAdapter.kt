package org.goafabric.personservice.adapter

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@RegisterReflectionForBinding(Callee::class)
@CircuitBreaker(name = "CalleeService")
class CalleeServiceAdapter (
    private val restTemplate: RestTemplate) {
    
    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${adapter.calleeservice.url}")
    private val url: String? = null

    fun sayMyName(name: String?): Callee? {
        log.info("Calling CalleService ...")
        val callee = restTemplate.getForObject("$url/callees/sayMyName?name={name}", Callee::class.java, name)
        log.info("got: $callee")
        return callee
    }
}