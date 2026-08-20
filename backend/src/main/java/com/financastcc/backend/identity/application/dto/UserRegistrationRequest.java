package com.financastcc.backend.identity.application.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record UserRegistrationRequest(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail deve ser válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String password,

        @NotBlank(message = "A confirmação da senha é obrigatória.")
        String passwordConfirmation
) {

    @AssertTrue(message = "A confirmação da senha deve ser igual à senha.")
    public boolean isPasswordConfirmationMatching() {
        return Objects.equals(password, passwordConfirmation);
    }
}
