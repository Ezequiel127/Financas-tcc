package com.financastcc.backend.identity.application.dto;

import java.time.Instant;

public record UserRegistrationResponse(
        Long id,
        String email,
        Instant createdAt
) {
}
