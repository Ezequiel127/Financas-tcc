package com.financastcc.backend.identity.application.exception;

public class InvalidPasswordResetTokenException extends RuntimeException {

    public InvalidPasswordResetTokenException() {
        super("Token de recuperação inválido ou expirado.");
    }
}
