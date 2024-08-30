package org.goafabric.personservice.extensions;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHandlerTest {
    private ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Test
    void handleIllegalArgumentException() {
        assertThat(exceptionHandler.handleIllegalArgumentException(new IllegalArgumentException("illegal argument")).getStatusCode())
                .isEqualTo(HttpStatus.PRECONDITION_FAILED);
    }

    @Test
    void handleIllegalStateException() {
        assertThat(exceptionHandler.handleIllegalStateException(new IllegalStateException("illegal state")).getStatusCode())
                .isEqualTo(HttpStatus.PRECONDITION_FAILED);
    }

    @Test
    void handleConstraintViolationException() {
        assertThat(exceptionHandler.handleConstraintValidationException(new ConstraintViolationException(new HashSet<>())).getStatusCode())
                .isEqualTo(HttpStatus.PRECONDITION_FAILED);
    }

    @Test
    void handleGeneralException() {
        assertThat(exceptionHandler.handleGeneralException(new IllegalStateException("general failure")).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}