package com.financastcc.backend.identity.application.dto;

public record CsrfTokenResponse(
        String headerName,
        String parameterName,
        String token
) {
}
