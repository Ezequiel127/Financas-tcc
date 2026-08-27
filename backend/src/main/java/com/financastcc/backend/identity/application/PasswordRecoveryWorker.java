package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.application.exception.PasswordResetEmailDeliveryException;
import com.financastcc.backend.identity.application.port.PasswordResetEmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordRecoveryWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordRecoveryWorker.class);

    private final PasswordRecoveryTokenIssuer tokenIssuer;
    private final PasswordResetEmailSender emailSender;

    public PasswordRecoveryWorker(
            PasswordRecoveryTokenIssuer tokenIssuer,
            PasswordResetEmailSender emailSender
    ) {
        this.tokenIssuer = tokenIssuer;
        this.emailSender = emailSender;
    }

    public void processRecovery(String normalizedEmail) {
        Optional<PasswordRecoveryDelivery> delivery = tokenIssuer.issue(normalizedEmail);
        if (delivery.isEmpty()) {
            return;
        }

        try {
            emailSender.sendPasswordResetInstructions(
                    delivery.get().recipient(),
                    delivery.get().resetLink()
            );
        } catch (PasswordResetEmailDeliveryException exception) {
            LOGGER.error("Falha operacional ao enviar instruções de recuperação de senha.");
        }
    }
}
