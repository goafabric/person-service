package org.goafabric.calleeservice.extensions

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.assertj.core.api.Assertions.assertThat
import org.goafabric.personservice.extensions.HttpInterceptor
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

internal class HttpInterceptorTest {
    private val httpInterceptor = HttpInterceptor()

    @Test
    fun preHandle() {
        assertThat(
            httpInterceptor.preHandle(
                mock(HttpServletRequest::class.java),
                mock(HttpServletResponse::class.java), Any()
            )
        ).isTrue()
    }

    @Test
    fun getTenantId() {
        assertThat(HttpInterceptor.getTenantId()).isEqualTo("0")
    }

    @Test
    fun getUserName() {
        assertThat(HttpInterceptor.getUserName()).isEqualTo("")
    }

}