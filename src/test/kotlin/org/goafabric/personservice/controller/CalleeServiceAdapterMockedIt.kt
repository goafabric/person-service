package org.goafabric.personservice.controller

import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.adapter.Callee
import org.goafabric.personservice.adapter.CalleeServiceAdapter
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.aot.DisabledInAotMode
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisabledInAotMode
class CalleeServiceAdapterMockedIt(
    @Autowired private val personController: PersonController) {

    @MockitoBean private val calleeServiceAdapter: CalleeServiceAdapter? = null //does not work via constructor injection

    @Test
    fun sayMyName() {
        whenever(calleeServiceAdapter!!.sayMyName(eq("Heisenberg"))).thenReturn(Callee("", "Heisenberg"))
        assertThat(personController.sayMyName(eq("Heisenberg"))).isNotNull
    }
}


