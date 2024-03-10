package org.goafabric.personservice.extensions

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class HttpInterceptorTest {
    private val httpInterceptor = HttpInterceptor()

    @Test
    fun preHandle() {
        assertThat(
            httpInterceptor.preHandle(
                mock(),
                mock(), Any()
            )
        ).isTrue()
    }

    @Test
    fun getTenantId() {
        assertThat(TenantContext.tenantId).isEqualTo("0")
    }

    @Test
    fun getUserName() {
        assertThat(TenantContext.userName).isEqualTo("anonymous")
    }

}