package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.application.dto.AuthenticatedUserResponse;
import com.financastcc.backend.identity.application.dto.UserLoginRequest;
import com.financastcc.backend.identity.application.exception.InvalidCredentialsException;
import com.financastcc.backend.identity.application.security.AuthenticatedUserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserAuthenticationService {

    private final AuthenticationManager authenticationManager;

    public UserAuthenticationService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Authentication authenticate(UserLoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        UsernamePasswordAuthenticationToken authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        normalizedEmail,
                        request.password()
                );

        try {
            return authenticationManager.authenticate(authenticationRequest);
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialsException();
        }
    }

    public AuthenticatedUserResponse toResponse(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new IllegalStateException("Principal autenticado inesperado.");
        }

        return new AuthenticatedUserResponse(principal.id(), principal.email());
    }
}
