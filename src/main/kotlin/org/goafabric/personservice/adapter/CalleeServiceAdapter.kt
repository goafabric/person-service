package org.goafabric.personservice.adapter

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@CircuitBreaker(name = "calleeservice")
interface CalleeServiceAdapter {
    @GetExchange("/callees/sayMyName")
    fun sayMyName(@RequestParam("name") name: String?): Callee?

    @GetExchange("/callees/sayMyOtherName/{name}")
    fun sayMyOtherName(@PathVariable("name") name: String?): Callee?
}
