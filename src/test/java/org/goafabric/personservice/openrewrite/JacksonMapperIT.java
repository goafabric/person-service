package org.goafabric.personservice.openrewrite;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JacksonMapperIT {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() {
        objectMapper.clearCaches();
    }
}
