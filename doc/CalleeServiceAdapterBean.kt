package org.goafabric.personservice.adapter

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.stereotype.Component

@CircuitBreaker(name = "calleeservice") @Component
class CalleeServiceAdapterBean(calleeServiceAdapter: CalleeServiceAdapter) : CalleeServiceAdapter by calleeServiceAdapter {}