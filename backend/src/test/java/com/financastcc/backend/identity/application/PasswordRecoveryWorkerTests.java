package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.application.exception.PasswordResetEmailDeliveryException;
import com.financastcc.backend.identity.application.port.PasswordResetEmailSender;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryWorkerTests {

    private static final String EMAIL = "user@example.com";
    private static final URI RESET_LINK = URI.create(
            "https://frontend.example/nova-senha?token=raw-reset-token"
    );

    @Mock
    private PasswordRecoveryTokenIssuer tokenIssuer;

    @Mock
    private PasswordResetEmailSender emailSender;

    @Test
    void tokenIssuanceIsTransactionalButWorkerAndSmtpAreOutsideThatTransaction()
            throws NoSuchMethodException {
        assertThat(PasswordRecoveryTokenIssuer.class
                .getDeclaredMethod("issue", String.class)
                .isAnnotationPresent(Transactional.class))
                .isTrue();
        assertThat(PasswordRecoveryWorker.class
                .getDeclaredMethod("processRecovery", String.class)
                .isAnnotationPresent(Transactional.class))
                .isFalse();

        boolean issuerDependsOnEmailSender = Arrays.stream(
                        PasswordRecoveryTokenIssuer.class.getDeclaredFields()
                )
                .anyMatch(field -> field.getType().equals(PasswordResetEmailSender.class));
        assertThat(issuerDependsOnEmailSender).isFalse();
    }

    @Test
    void successfulIssuanceReturnsBeforeSmtpIsCalled() {
        PasswordRecoveryDelivery delivery =
                new PasswordRecoveryDelivery(EMAIL, RESET_LINK);
        when(tokenIssuer.issue(EMAIL)).thenReturn(Optional.of(delivery));

        worker().processRecovery(EMAIL);

        InOrder callOrder = inOrder(tokenIssuer, emailSender);
        callOrder.verify(tokenIssuer).issue(EMAIL);
        callOrder.verify(emailSender).sendPasswordResetInstructions(EMAIL, RESET_LINK);
    }

    @Test
    void issuanceOrPersistenceFailurePreventsSmtpAndPropagatesToAsyncProcessor() {
        doThrow(new RuntimeException("internal database detail"))
                .when(tokenIssuer)
                .issue(EMAIL);

        assertThatThrownBy(() -> worker().processRecovery(EMAIL))
                .isInstanceOf(RuntimeException.class);

        verifyNoInteractions(emailSender);
    }

    @Test
    void smtpFailureAfterIssuanceRemainsContainedInBackground() {
        when(tokenIssuer.issue(EMAIL)).thenReturn(Optional.of(
                new PasswordRecoveryDelivery(EMAIL, RESET_LINK)
        ));
        doThrow(new PasswordResetEmailDeliveryException())
                .when(emailSender)
                .sendPasswordResetInstructions(EMAIL, RESET_LINK);

        assertThatCode(() -> worker().processRecovery(EMAIL)).doesNotThrowAnyException();

        verify(tokenIssuer).issue(EMAIL);
    }

    @Test
    void unknownUserProducesNoEmail() {
        when(tokenIssuer.issue("unknown@example.com")).thenReturn(Optional.empty());

        worker().processRecovery("unknown@example.com");

        verify(tokenIssuer).issue("unknown@example.com");
        verifyNoInteractions(emailSender);
    }

    private PasswordRecoveryWorker worker() {
        return new PasswordRecoveryWorker(tokenIssuer, emailSender);
    }
}
