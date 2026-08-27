package com.financastcc.backend.identity.presentation;

import com.financastcc.backend.identity.application.PasswordRecoveryService;
import com.financastcc.backend.identity.application.PasswordResetService;
import com.financastcc.backend.identity.application.dto.PasswordRecoveryRequest;
import com.financastcc.backend.identity.application.dto.PasswordRecoveryResponse;
import com.financastcc.backend.identity.application.dto.PasswordResetRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class PasswordRecoveryController {

    private static final String NEUTRAL_RECOVERY_MESSAGE =
            "Se existir uma conta para este e-mail, enviaremos as instruções de recuperação.";

    private final PasswordRecoveryService passwordRecoveryService;
    private final PasswordResetService passwordResetService;

    public PasswordRecoveryController(
            PasswordRecoveryService passwordRecoveryService,
            PasswordResetService passwordResetService
    ) {
        this.passwordRecoveryService = passwordRecoveryService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/password-recovery")
    public ResponseEntity<PasswordRecoveryResponse> requestRecovery(
            @Valid @RequestBody PasswordRecoveryRequest request
    ) {
        passwordRecoveryService.requestRecovery(request.email());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new PasswordRecoveryResponse(NEUTRAL_RECOVERY_MESSAGE));
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody PasswordResetRequest request
    ) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}
