package com.financastcc.backend.identity.application;

import com.financastcc.backend.identity.application.dto.UserRegistrationRequest;
import com.financastcc.backend.identity.application.dto.UserRegistrationResponse;
import com.financastcc.backend.identity.application.exception.EmailAlreadyExistsException;
import com.financastcc.backend.identity.domain.User;
import com.financastcc.backend.identity.infrastructure.UserRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserRegistrationService {

    private static final String UNIQUE_EMAIL_CONSTRAINT = "uq_users_email_ci";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserRegistrationResponse register(UserRegistrationRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException();
        }

        String passwordHash = passwordEncoder.encode(request.password());
        User user = new User(normalizedEmail, passwordHash);

        try {
            User savedUser = userRepository.saveAndFlush(user);
            return new UserRegistrationResponse(
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getCreatedAt()
            );
        } catch (DataIntegrityViolationException exception) {
            if (isUniqueEmailConstraintViolation(exception)) {
                throw new EmailAlreadyExistsException();
            }
            throw exception;
        }
    }

    private boolean isUniqueEmailConstraintViolation(DataIntegrityViolationException exception) {
        Throwable cause = exception.getCause();

        while (cause != null) {
            if (cause instanceof ConstraintViolationException constraintViolation
                    && UNIQUE_EMAIL_CONSTRAINT.equals(constraintViolation.getConstraintName())) {
                return true;
            }
            cause = cause.getCause();
        }

        return false;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
