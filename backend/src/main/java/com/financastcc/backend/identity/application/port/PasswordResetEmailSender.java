package com.financastcc.backend.identity.application.port;

import java.net.URI;

public interface PasswordResetEmailSender {

    void sendPasswordResetInstructions(String recipient, URI resetLink);
}
