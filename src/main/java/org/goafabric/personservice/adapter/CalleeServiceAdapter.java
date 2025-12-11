package org.goafabric.personservice.adapter;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

//@CircuitBreaker(name = "calleeservice")
//@MyCircuitBreaker(name = "calleeservice")
public interface CalleeServiceAdapter {

    @GetExchange("/callees/sayMyName")
    Callee sayMyName(@RequestParam("name") String name);

    @GetExchange("/callees/sayMyOtherName/{name}")
    Callee sayMyOtherName(@PathVariable("name") String name);

}
