package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.application.port.PasswordResetEmailSender;
import com.financastcc.backend.identity.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTests {

    @Mock
    private PasswordRecoveryAsyncProcessor asyncProcessor;

    @Test
    void synchronousServiceHasNoUserRepositoryDependencyOrTransaction()
            throws NoSuchMethodException {
        boolean hasUserRepositoryDependency = Arrays.stream(
                        PasswordRecoveryService.class.getDeclaredFields()
                )
                .anyMatch(field -> field.getType().equals(UserRepository.class));
        boolean hasEmailSenderDependency = Arrays.stream(
                        PasswordRecoveryService.class.getDeclaredFields()
                )
                .anyMatch(field -> field.getType().equals(PasswordResetEmailSender.class));
        assertThat(hasUserRepositoryDependency).isFalse();
        assertThat(hasEmailSenderDependency).isFalse();
        assertThat(PasswordRecoveryService.class
                .getDeclaredMethod("requestRecovery", String.class)
                .isAnnotationPresent(Transactional.class))
                .isFalse();
    }

    @Test
    void submitsSameAsyncOperationForEveryValidatedNormalizedEmail() {
        PasswordRecoveryService service = service();

        service.requestRecovery("existing@example.com");
        service.requestRecovery("unknown@example.com");

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        verify(asyncProcessor, times(2)).processRecovery(emailCaptor.capture());
        assertThat(emailCaptor.getAllValues()).containsExactly(
                "existing@example.com",
                "unknown@example.com"
        );
    }

    @Test
    void taskSubmissionRejectionDoesNotEscapeToCaller() {
        doThrow(new TaskRejectedException("executor queue is full"))
                .when(asyncProcessor)
                .processRecovery("user@example.com");

        assertThatCode(() -> service().requestRecovery("user@example.com"))
                .doesNotThrowAnyException();
    }

    private PasswordRecoveryService service() {
        return new PasswordRecoveryService(asyncProcessor);
    }
}
