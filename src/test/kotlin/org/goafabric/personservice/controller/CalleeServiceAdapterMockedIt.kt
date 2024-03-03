/*
package org.goafabric.personservice.controller

import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.adapter.Callee
import org.goafabric.personservice.adapter.CalleeServiceAdapter
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.aot.DisabledInAotMode

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInAotMode
class CalleeServiceAdapterMockedIt(
    @Autowired private val personController: PersonController,
    @MockBean private val calleeServiceAdapter: CalleeServiceAdapter) {

    @Test
    fun sayMyName() {
        `when`(calleeServiceAdapter.sayMyName(eq("Heisenberg"))).thenReturn(Callee("", "Heisenberg"))
        assertThat(personController.sayMyName(eq("Heisenberg"))).isNotNull
    }
}

 */
