package org.goafabric.personservice.adapter;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;

@Configuration
@Slf4j
public class CalleeServiceConfiguration {

    @Bean
    public RestTemplate restTemplate(
            @Value("${adapter.calleeservice.user}") String user,
            @Value("${adapter.calleeservice.password}") String password,
            @Value("${adapter.timeout}") Integer timeout) {


        final RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(timeout))
                .setReadTimeout(Duration.ofMillis(timeout))
                //.requestFactory(this::sslRequestFactory)
                .build();

        addBasicAuthAndStuff(user, password, restTemplate);
        return restTemplate;
    }

    private void addBasicAuthAndStuff(String user, String password, RestTemplate restTemplate) {
        restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter()));
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            request.getHeaders().setBasicAuth(
                    new String(Base64.getDecoder().decode(user)), new String(Base64.getDecoder().decode(password)));
            return execution.execute(request, body);
        });
    }

    @Value("${my.trust-store}")
    private String trustStore;

    @Value("${my.trust-store-password}")
    private String trustStorePassword;

    @SneakyThrows
    private HttpComponentsClientHttpRequestFactory sslRequestFactory() {
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(ResourceUtils.getFile(trustStore), trustStorePassword.toCharArray())
                    //.loadTrustMaterial(null, new TrustAllStrategy())
                    .build();

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
            HttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(socketFactory)
                    .build();
            return new HttpComponentsClientHttpRequestFactory(httpClient);
        } catch (Exception e) {
            log.error("cannot initialize ssl !", e);
            return null;
        }
    }
    
}
