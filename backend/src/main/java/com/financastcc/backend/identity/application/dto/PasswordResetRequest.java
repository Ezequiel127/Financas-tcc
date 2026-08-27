package com.financastcc.backend.identity.application.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record PasswordResetRequest(
        @NotBlank(message = "O token de recuperação é obrigatório.")
        String token,

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
