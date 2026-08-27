package com.financastcc.backend.identity.infrastructure;

import com.financastcc.backend.identity.application.exception.PasswordResetEmailDeliveryException;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SmtpPasswordResetEmailSenderTests {

    @Test
    void sendsPlainTextPurposeLinkAndThirtyMinuteExpiration() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        SmtpPasswordResetEmailSender sender =
                new SmtpPasswordResetEmailSender(mailSender, "no-reply@example.com");
        URI resetLink = URI.create(
                "https://frontend.example/nova-senha?token=raw-reset-token"
        );

        sender.sendPasswordResetInstructions("user@example.com", resetLink);

        verify(mailSender).send(org.mockito.ArgumentMatchers.argThat(
                (SimpleMailMessage message) -> {
                    assertThat(message.getFrom()).isEqualTo("no-reply@example.com");
                    assertThat(message.getTo()).containsExactly("user@example.com");
                    assertThat(message.getSubject()).isEqualTo("Recuperação de senha");
                    assertThat(message.getText())
                            .contains("redefinir a senha")
                            .contains(resetLink.toString())
                            .contains("expira em 30 minutos");
                    return true;
                }
        ));
    }

    @Test
    void translatesMailFailureWithoutExposingSmtpDetails() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        SmtpPasswordResetEmailSender sender =
                new SmtpPasswordResetEmailSender(mailSender, "no-reply@example.com");
        doThrow(new MailSendException("sensitive SMTP detail"))
                .when(mailSender)
                .send(org.mockito.ArgumentMatchers.any(SimpleMailMessage.class));

        assertThatThrownBy(() -> sender.sendPasswordResetInstructions(
                "user@example.com",
                URI.create("https://frontend.example/nova-senha?token=raw-reset-token")
        ))
                .isInstanceOf(PasswordResetEmailDeliveryException.class)
                .hasMessage(null);
    }
}
