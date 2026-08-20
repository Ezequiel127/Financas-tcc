package com.financastcc.backend.identity.application.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRegistrationRequestValidationTests {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsInvalidBasicInput() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "invalid-email",
                " ",
                ""
        );

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("email", "password", "passwordConfirmation");
    }

    @Test
    void rejectsDifferentPasswordConfirmation() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "user@example.com",
                "password-one",
                "password-two"
        );

        assertThat(validator.validate(request))
                .anyMatch(violation -> violation.getMessage().contains("igual à senha"));
    }
}
