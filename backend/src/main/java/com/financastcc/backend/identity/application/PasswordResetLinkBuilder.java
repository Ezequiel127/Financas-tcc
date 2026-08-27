package com.financastcc.backend.identity.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class PasswordResetLinkBuilder {

    private static final String RESET_PATH = "/nova-senha";

    private final URI frontendBaseUrl;

    public PasswordResetLinkBuilder(
            @Value("${app.password-reset.frontend-base-url}") URI frontendBaseUrl
    ) {
        if (!frontendBaseUrl.isAbsolute()
                || frontendBaseUrl.getHost() == null
                || !("http".equalsIgnoreCase(frontendBaseUrl.getScheme())
                || "https".equalsIgnoreCase(frontendBaseUrl.getScheme()))) {
            throw new IllegalArgumentException(
                    "A URL base do frontend deve ser uma URL HTTP(S) absoluta."
            );
        }
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public URI build(String rawToken) {
        String basePath = frontendBaseUrl.getPath() == null
                ? ""
                : frontendBaseUrl.getPath().replaceFirst("/+$", "");

        return UriComponentsBuilder.fromUri(frontendBaseUrl)
                .replacePath(basePath + RESET_PATH)
                .replaceQuery(null)
                .fragment(null)
                .queryParam("token", rawToken)
                .build()
                .encode()
                .toUri();
    }
}
