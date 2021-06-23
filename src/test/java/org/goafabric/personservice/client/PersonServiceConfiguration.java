package org.goafabric.personservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Collections;

@Configuration
@PropertySource("test.properties")
public class PersonServiceConfiguration {
    @Bean
    public RestTemplate restTemplateTest(
        @Value("${adapter.personservice.user}") String user,
        @Value("${adapter.personservice.password}") String password,
        @Value("${adapter.timeout}") Integer timeout) {
        final RestTemplate restTemplate = new RestTemplateBuilder()
                //.setConnectTimeout(Duration.ofMillis(timeout))
                //.setReadTimeout(Duration.ofMillis(timeout))
                .build();

        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            request.getHeaders().setBasicAuth(
                    new String(Base64.getDecoder().decode(user)), new String(Base64.getDecoder().decode(password)));
            return execution.execute(request, body);
        });
        return restTemplate;
    }

}
