package org.goafabric.personservice.controller;

import org.goafabric.personservice.adapter.Callee;
import org.goafabric.personservice.adapter.CalleeServiceAdapter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.aot.DisabledInAotMode;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInAotMode
public class CalleeServiceAdapterMockedIT {
    @Autowired
    private PersonController personController;

    @MockBean
    private CalleeServiceAdapter calleeServiceAdapter;

    @Test
    void sayMyName() {
        Mockito.when(calleeServiceAdapter.sayMyName("Heisenberg")).thenReturn(new Callee("", "Heisenberg"));
        assertThat(personController.sayMyName("Heisenberg")).isNotNull();
    }
}
