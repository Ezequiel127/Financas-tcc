package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.domain.PasswordResetToken;
import com.financastcc.backend.identity.domain.User;
import com.financastcc.backend.identity.infrastructure.PasswordResetTokenRepository;
import com.financastcc.backend.identity.infrastructure.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PasswordRecoveryTokenIssuer {

    private static final Duration TOKEN_VALIDITY = Duration.ofMinutes(30);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenManager tokenManager;
    private final PasswordResetLinkBuilder linkBuilder;

    public PasswordRecoveryTokenIssuer(
            UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetTokenManager tokenManager,
            PasswordResetLinkBuilder linkBuilder
    ) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.tokenManager = tokenManager;
        this.linkBuilder = linkBuilder;
    }

    @Transactional
    public Optional<PasswordRecoveryDelivery> issue(String normalizedEmail) {
        Optional<User> user = userRepository.findByEmail(normalizedEmail);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        String rawToken = tokenManager.generateRawToken();
        String tokenHash = tokenManager.hash(rawToken);
        Instant createdAt = Instant.now();
        PasswordResetToken resetToken = new PasswordResetToken(
                user.get(),
                tokenHash,
                createdAt.plus(TOKEN_VALIDITY),
                createdAt
        );
        passwordResetTokenRepository.saveAndFlush(resetToken);

        URI resetLink = linkBuilder.build(rawToken);
        return Optional.of(new PasswordRecoveryDelivery(user.get().getEmail(), resetLink));
    }
}
