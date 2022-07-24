package org.goafabric.personservice.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class CalleeServiceAdapter {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${adapter.calleeservice.url}")
    private String url;

    //@Autowired
    //private CircuitBreakerFactory cbFactory;


    public Callee sayMyName(String name) {
        log.info("Calling CalleService ...");
        final Callee callee =
                //cbFactory.create(this.getClass().getSimpleName()).run(() ->
                restTemplate.getForObject(url + "/callees/sayMyName?name={name}", Callee.class, name);
                //,(throwable) -> {
                //    throw new NoFallbackAvailableException(throwable.getMessage(), throwable);}
                //});
        log.info("got: " + callee);
        return callee;
    }
}
