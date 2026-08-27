package com.financastcc.backend.identity.infrastructure;

import com.financastcc.backend.identity.application.exception.PasswordResetEmailDeliveryException;
import com.financastcc.backend.identity.application.port.PasswordResetEmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class SmtpPasswordResetEmailSender implements PasswordResetEmailSender {

    private final JavaMailSender mailSender;
    private final String sender;

    public SmtpPasswordResetEmailSender(
            JavaMailSender mailSender,
            @Value("${app.password-reset.mail-from}") String sender
    ) {
        this.mailSender = mailSender;
        this.sender = sender;
    }

    @Override
    public void sendPasswordResetInstructions(String recipient, URI resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject("Recuperação de senha");
        message.setText("""
                Recebemos uma solicitação para redefinir a senha da sua conta.

                Acesse o link abaixo para criar uma nova senha:
                %s

                Este link expira em 30 minutos.
                Se você não fez esta solicitação, ignore este e-mail.
                """.formatted(resetLink));

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            throw new PasswordResetEmailDeliveryException();
        }
    }
}
