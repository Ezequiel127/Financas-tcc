package com.financastcc.backend.identity.application.dto;

public record AuthenticatedUserResponse(
        Long id,
        String email
) {
}
