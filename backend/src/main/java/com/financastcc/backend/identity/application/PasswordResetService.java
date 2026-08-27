package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.application.dto.PasswordResetRequest;
import com.financastcc.backend.identity.application.exception.InvalidPasswordResetTokenException;
import com.financastcc.backend.identity.domain.PasswordResetToken;
import com.financastcc.backend.identity.domain.User;
import com.financastcc.backend.identity.infrastructure.PasswordResetTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetTokenManager tokenManager;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordResetTokenManager tokenManager,
            PasswordEncoder passwordEncoder
    ) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.tokenManager = tokenManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        String tokenHash = tokenManager.hash(request.token());
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(InvalidPasswordResetTokenException::new);
        User user = resetToken.getUser();

        int consumedTokens = passwordResetTokenRepository.consumeIfAvailable(
                resetToken.getId(),
                Instant.now()
        );
        if (consumedTokens != 1) {
            throw new InvalidPasswordResetTokenException();
        }

        String newPasswordHash = passwordEncoder.encode(request.password());
        user.changePasswordHash(newPasswordHash);
    }
}
