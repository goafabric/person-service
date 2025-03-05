package org.goafabric.personservice.base.extensions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.goafabric.personservice.base.extensions.HttpInterceptor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class HttpInterceptorTest {
    private HttpInterceptor httpInterceptor = new HttpInterceptor();

    @Test
    void preHandle() {
        assertThat(httpInterceptor.preHandle(
                Mockito.mock(HttpServletRequest.class)
                , Mockito.mock(HttpServletResponse.class), new Object()))
                .isTrue();
    }

}