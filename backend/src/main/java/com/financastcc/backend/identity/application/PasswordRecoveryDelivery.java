package com.financastcc.backend.identity.application;

import java.net.URI;

public record PasswordRecoveryDelivery(
        String recipient,
        URI resetLink
) {
}
