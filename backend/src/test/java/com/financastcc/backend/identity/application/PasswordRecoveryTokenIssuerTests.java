package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.domain.PasswordResetToken;
import com.financastcc.backend.identity.domain.User;
import com.financastcc.backend.identity.infrastructure.PasswordResetTokenRepository;
import com.financastcc.backend.identity.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryTokenIssuerTests {

    private static final String EMAIL = "user@example.com";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordResetTokenManager tokenManager = new PasswordResetTokenManager();

    @Test
    void existingUserProducesOnlyHashedPersistenceAndInMemoryDelivery() {
        User user = new User(EMAIL, "current-password-hash");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        PasswordRecoveryDelivery delivery = issuer().issue(EMAIL).orElseThrow();

        ArgumentCaptor<PasswordResetToken> tokenCaptor =
                ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(userRepository).findByEmail(EMAIL);
        verify(passwordResetTokenRepository).saveAndFlush(tokenCaptor.capture());

        PasswordResetToken persistedToken = tokenCaptor.getValue();
        String rawToken = UriComponentsBuilder.fromUri(delivery.resetLink())
                .build()
                .getQueryParams()
                .getFirst("token");

        assertThat(delivery.recipient()).isEqualTo(EMAIL);
        assertThat(rawToken).isNotBlank().matches("[A-Za-z0-9_-]{43}");
        assertThat(persistedToken.getTokenHash())
                .isNotEqualTo(rawToken)
                .isEqualTo(tokenManager.hash(rawToken))
                .hasSize(64);
        assertThat(Duration.between(
                persistedToken.getCreatedAt(),
                persistedToken.getExpiresAt()
        )).isEqualTo(Duration.ofMinutes(30));
        assertThat(persistedToken.getUsedAt()).isNull();
        assertThat(delivery.resetLink().toString())
                .startsWith("https://frontend.example/app/nova-senha?token=");
    }

    @Test
    void unknownUserDoesNotCreateTokenOrDelivery() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Optional<PasswordRecoveryDelivery> delivery =
                issuer().issue("unknown@example.com");

        assertThat(delivery).isEmpty();
        verify(userRepository).findByEmail("unknown@example.com");
        verifyNoInteractions(passwordResetTokenRepository);
    }

    private PasswordRecoveryTokenIssuer issuer() {
        return new PasswordRecoveryTokenIssuer(
                userRepository,
                passwordResetTokenRepository,
                tokenManager,
                new PasswordResetLinkBuilder(URI.create("https://frontend.example/app/"))
        );
    }
}
