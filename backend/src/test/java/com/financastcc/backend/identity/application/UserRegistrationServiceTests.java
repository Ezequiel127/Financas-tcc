package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.application.dto.UserRegistrationRequest;
import com.financastcc.backend.identity.application.dto.UserRegistrationResponse;
import com.financastcc.backend.identity.application.exception.EmailAlreadyExistsException;
import com.financastcc.backend.identity.domain.User;
import com.financastcc.backend.identity.infrastructure.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.SQLException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void registersUserWithNormalizedEmailAndEncodedPassword() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "  USER@Example.COM  ",
                "plain-password",
                "plain-password"
        );
        Instant createdAt = Instant.parse("2026-08-20T12:00:00Z");
        User savedUser = mock(User.class);

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(User.class)))
                .thenReturn(savedUser);
        when(savedUser.getId()).thenReturn(1L);
        when(savedUser.getEmail()).thenReturn("user@example.com");
        when(savedUser.getCreatedAt()).thenReturn(createdAt);

        UserRegistrationResponse response = service().register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        User userToPersist = userCaptor.getValue();

        assertThat(userToPersist.getEmail()).isEqualTo("user@example.com");
        assertThat(userToPersist.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(response).isEqualTo(
                new UserRegistrationResponse(1L, "user@example.com", createdAt)
        );
    }

    @Test
    void rejectsEmailThatAlreadyExists() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "user@example.com",
                "plain-password",
                "plain-password"
        );
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service().register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(passwordEncoder, never()).encode(org.mockito.ArgumentMatchers.anyString());
        verify(userRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void translatesUniqueConstraintRaceToDuplicateEmailConflict() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "user@example.com",
                "plain-password",
                "plain-password"
        );
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        ConstraintViolationException uniqueEmailViolation = new ConstraintViolationException(
                "unique constraint violation",
                new SQLException("duplicate key", "23505"),
                "uq_users_email_ci"
        );
        when(userRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(User.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "data integrity violation",
                        uniqueEmailViolation
                ));

        assertThatThrownBy(() -> service().register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void propagatesUnrelatedDataIntegrityViolation() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "user@example.com",
                "plain-password",
                "plain-password"
        );
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        ConstraintViolationException unrelatedConstraintViolation = new ConstraintViolationException(
                "check constraint violation",
                new SQLException("check violation", "23514"),
                "ck_users_email_normalized"
        );
        DataIntegrityViolationException originalException = new DataIntegrityViolationException(
                "data integrity violation",
                unrelatedConstraintViolation
        );
        when(userRepository.saveAndFlush(org.mockito.ArgumentMatchers.any(User.class)))
                .thenThrow(originalException);

        assertThatThrownBy(() -> service().register(request))
                .isSameAs(originalException);
    }

    private UserRegistrationService service() {
        return new UserRegistrationService(userRepository, passwordEncoder);
    }
}
