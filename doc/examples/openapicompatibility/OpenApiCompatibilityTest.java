package org.goafabric.personservice.architecture;

import org.junit.jupiter.api.Test;
import org.openapitools.openapidiff.core.OpenApiCompare;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

//testImplementation("org.openapitools.openapidiff:openapi-diff-core:2.1.2")
//testImplementation("javax.xml.bind:jaxb-api:2.3.1")
//testImplementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OpenApiCompatibilityTest {
    @LocalServerPort
    String port;
    
    @Test
    void shouldNotHaveBreakingChanges() throws IOException {
        var openApiUrl = "http://localhost:" + port + "/v3/api-docs";

        var diff = OpenApiCompare.fromLocations("doc/generated/openapi.json", openApiUrl);

        //System.out.println(new RestTemplate().getForObject(openApiUrl, String.class));
        //diff.getChangedElements().forEach(c -> System.out.println(c.toString()));

        Files.write(Paths.get( "oldspec.json"), diff.getOldSpecOpenApi().toString().getBytes());
        Files.write(Paths.get( "newspec.json"), diff.getNewSpecOpenApi().toString().getBytes());

        assertTrue(!diff.isIncompatible(), "Breaking changes detected in OpenAPI contract!");
    }
}
