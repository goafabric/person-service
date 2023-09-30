package org.goafabric.personservice.adapter

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@RegisterReflectionForBinding(Callee::class)
@CircuitBreaker(name = "CalleeService")
class CalleeServiceAdapter {
    @Autowired
    private val restTemplate: RestTemplate? = null

    @Value("\${adapter.calleeservice.url}")
    private val url: String? = null
    fun sayMyName(name: String?): Callee? {
        return restTemplate!!.getForObject("$url/callees/sayMyName?name={name}", Callee::class.java, name)
    }
}
