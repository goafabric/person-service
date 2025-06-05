package org.goafabric.personservice.remote;

import jakarta.validation.Valid;
import org.goafabric.personservice.controller.dto.Person;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@HttpExchange(value = "/persons", contentType = MediaType.APPLICATION_JSON_VALUE)
public interface PersonControllerRemote {

    @GetExchange("{id}")
    Person getById(@PathVariable("id") String id);

    //of course stupid restClient does not support @ModelAttribute so we have to manually list the RequestParams here
    @GetExchange
    List<Person> find(@RequestParam(value = "firstName", required = false) String firstName,
                      @RequestParam(value = "lastName", required = false) String lastName,
                      @RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetExchange("street")
    List<Person> findByStreet(@RequestParam("street") String street, @RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @GetExchange("name")
    Person sayMyName (@RequestParam("name") String name);

    @PostExchange
    Person save(@RequestBody @Valid Person person);


}
