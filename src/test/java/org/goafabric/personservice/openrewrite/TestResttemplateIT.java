package org.goafabric.personservice.openrewrite;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate        
class TestResttemplateIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void test() {
        testRestTemplate.toString();
    }
}
