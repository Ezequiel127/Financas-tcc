package com.financastcc.backend.identity.infrastructure;

import com.financastcc.backend.identity.domain.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
            UPDATE PasswordResetToken token
               SET token.usedAt = :now
             WHERE token.id = :id
               AND token.usedAt IS NULL
               AND token.expiresAt > :now
            """)
    int consumeIfAvailable(@Param("id") Long id, @Param("now") Instant now);
}
