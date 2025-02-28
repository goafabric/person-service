package org.goafabric.personservice.controller;

import org.goafabric.personservice.base.adapter.Callee;
import org.goafabric.personservice.base.adapter.CalleeServiceAdapter;
import org.goafabric.personservice.v1.controller.PersonController;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInAotMode
class CalleeServiceAdapterMockedIT {
    @Autowired
    private PersonController personController;

    @MockitoBean
    private CalleeServiceAdapter calleeServiceAdapter;

    @Test
    void sayMyName() {
        Mockito.when(calleeServiceAdapter.sayMyName("Heisenberg")).thenReturn(new Callee("", "Heisenberg"));
        assertThat(personController.sayMyName("Heisenberg")).isNotNull();
    }
}
