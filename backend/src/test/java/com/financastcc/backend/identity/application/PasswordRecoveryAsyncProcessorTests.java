package com.financastcc.backend.identity.application;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Async;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryAsyncProcessorTests {

    @Mock
    private PasswordRecoveryWorker worker;

    @Test
    void usesDedicatedPasswordRecoveryExecutor() throws NoSuchMethodException {
        Async async = PasswordRecoveryAsyncProcessor.class
                .getDeclaredMethod("processRecovery", String.class)
                .getAnnotation(Async.class);

        assertThat(async).isNotNull();
        assertThat(async.value()).isEqualTo(PasswordRecoveryAsyncProcessor.EXECUTOR_NAME);
    }

    @Test
    void backgroundRuntimeFailureDoesNotEscapeAsyncProcessor() {
        doThrow(new RuntimeException("internal persistence detail"))
                .when(worker)
                .processRecovery("user@example.com");
        PasswordRecoveryAsyncProcessor processor =
                new PasswordRecoveryAsyncProcessor(worker);

        assertThatCode(() -> processor.processRecovery("user@example.com"))
                .doesNotThrowAnyException();
    }
}
