package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.application.dto.PasswordResetRequest;
import com.financastcc.backend.identity.application.exception.InvalidPasswordResetTokenException;
import com.financastcc.backend.identity.domain.PasswordResetToken;
import com.financastcc.backend.identity.domain.User;
import com.financastcc.backend.identity.infrastructure.PasswordResetTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTests {

    private static final String RAW_TOKEN = "valid-raw-token";
    private static final String NEW_PASSWORD = "new-password";
    private static final String GENERIC_ERROR = "Token de recuperação inválido ou expirado.";

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordResetTokenManager tokenManager = new PasswordResetTokenManager();

    @Test
    void validTokenChangesPasswordWithBcryptAndIsConsumedOnce() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User("user@example.com", passwordEncoder.encode("old-password"));
        PasswordResetToken token = tokenFor(user);
        configureFoundToken(token);
        when(passwordResetTokenRepository.consumeIfAvailable(
                org.mockito.ArgumentMatchers.eq(7L),
                any(Instant.class)
        )).thenReturn(1);

        service(passwordEncoder).resetPassword(request(NEW_PASSWORD));

        assertThat(passwordEncoder.matches(NEW_PASSWORD, user.getPasswordHash())).isTrue();
        assertThat(user.getPasswordHash()).isNotEqualTo(NEW_PASSWORD);
        verify(passwordResetTokenRepository, times(1)).consumeIfAvailable(
                org.mockito.ArgumentMatchers.eq(7L),
                any(Instant.class)
        );
    }

    @Test
    void tokenCanOnlyBeConsumedOnce() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User("user@example.com", passwordEncoder.encode("old-password"));
        PasswordResetToken token = tokenFor(user);
        configureFoundToken(token);
        when(passwordResetTokenRepository.consumeIfAvailable(
                org.mockito.ArgumentMatchers.eq(7L),
                any(Instant.class)
        )).thenReturn(1, 0);
        PasswordResetService service = service(passwordEncoder);

        service.resetPassword(request("first-new-password"));
        String hashAfterFirstReset = user.getPasswordHash();

        assertThatThrownBy(() -> service.resetPassword(request("second-new-password")))
                .isInstanceOf(InvalidPasswordResetTokenException.class)
                .hasMessage(GENERIC_ERROR);
        assertThat(user.getPasswordHash()).isEqualTo(hashAfterFirstReset);
        assertThat(passwordEncoder.matches("first-new-password", user.getPasswordHash())).isTrue();
        assertThat(passwordEncoder.matches("second-new-password", user.getPasswordHash())).isFalse();
    }

    @Test
    void usedTokenIsRejectedWithGenericError() {
        assertUnavailableTokenRejectedWithGenericError();
    }

    @Test
    void expiredTokenIsRejectedWithGenericError() {
        assertUnavailableTokenRejectedWithGenericError();
    }

    @Test
    void unknownTokenIsRejectedWithSameGenericError() {
        when(passwordResetTokenRepository.findByTokenHash(tokenManager.hash(RAW_TOKEN)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service(new BCryptPasswordEncoder())
                .resetPassword(request(NEW_PASSWORD)))
                .isInstanceOf(InvalidPasswordResetTokenException.class)
                .hasMessage(GENERIC_ERROR);
    }

    @Test
    void failedAtomicConsumptionDoesNotChangePassword() {
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        User user = new User("user@example.com", "unchanged-password-hash");
        PasswordResetToken token = tokenFor(user);
        configureFoundToken(token);
        when(passwordResetTokenRepository.consumeIfAvailable(
                org.mockito.ArgumentMatchers.eq(7L),
                any(Instant.class)
        )).thenReturn(0);

        assertThatThrownBy(() -> service(passwordEncoder).resetPassword(request(NEW_PASSWORD)))
                .isInstanceOf(InvalidPasswordResetTokenException.class)
                .hasMessage(GENERIC_ERROR);

        assertThat(user.getPasswordHash()).isEqualTo("unchanged-password-hash");
        verify(passwordEncoder, never()).encode(any());
    }

    private void assertUnavailableTokenRejectedWithGenericError() {
        User user = new User("user@example.com", "unchanged-password-hash");
        PasswordResetToken token = tokenFor(user);
        configureFoundToken(token);
        when(passwordResetTokenRepository.consumeIfAvailable(
                org.mockito.ArgumentMatchers.eq(7L),
                any(Instant.class)
        )).thenReturn(0);

        assertThatThrownBy(() -> service(new BCryptPasswordEncoder())
                .resetPassword(request(NEW_PASSWORD)))
                .isInstanceOf(InvalidPasswordResetTokenException.class)
                .hasMessage(GENERIC_ERROR);
    }

    private PasswordResetToken tokenFor(User user) {
        PasswordResetToken token = mock(PasswordResetToken.class);
        when(token.getId()).thenReturn(7L);
        when(token.getUser()).thenReturn(user);
        return token;
    }

    private void configureFoundToken(PasswordResetToken token) {
        when(passwordResetTokenRepository.findByTokenHash(tokenManager.hash(RAW_TOKEN)))
                .thenReturn(Optional.of(token));
    }

    private PasswordResetService service(PasswordEncoder passwordEncoder) {
        return new PasswordResetService(
                passwordResetTokenRepository,
                tokenManager,
                passwordEncoder
        );
    }

    private PasswordResetRequest request(String password) {
        return new PasswordResetRequest(RAW_TOKEN, password, password);
    }
}
