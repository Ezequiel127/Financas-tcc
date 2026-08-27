package com.financastcc.backend.identity.presentation;

import com.financastcc.backend.identity.application.PasswordRecoveryService;
import com.financastcc.backend.identity.application.PasswordResetService;
import com.financastcc.backend.identity.application.exception.InvalidPasswordResetTokenException;
import com.financastcc.backend.identity.infrastructure.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PasswordRecoveryController.class)
@Import({SecurityConfiguration.class, RegistrationExceptionHandler.class})
class PasswordRecoveryControllerTests {

    private static final String NEUTRAL_MESSAGE =
            "Se existir uma conta para este e-mail, enviaremos as instruções de recuperação.";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PasswordRecoveryService passwordRecoveryService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void recoveryRequiresCsrf() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recoveryRequest("user@example.com")))
                .andExpect(status().isForbidden());
    }

    @Test
    void existingAndUnknownEmailsReceiveIdenticalNeutralResponse() throws Exception {
        MvcResult existingResponse = mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recoveryRequest("existing@example.com")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value(NEUTRAL_MESSAGE))
                .andReturn();

        MvcResult unknownResponse = mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recoveryRequest("unknown@example.com")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value(NEUTRAL_MESSAGE))
                .andReturn();

        assertThat(unknownResponse.getResponse().getStatus())
                .isEqualTo(existingResponse.getResponse().getStatus());
        assertThat(unknownResponse.getResponse().getContentAsString())
                .isEqualTo(existingResponse.getResponse().getContentAsString());
    }

    @Test
    void recoveryDoesNotForwardUntrustedRequestHeaders() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .with(csrf())
                        .header("Host", "attacker.example")
                        .header("Origin", "https://attacker.example")
                        .header("Referer", "https://attacker.example/form")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recoveryRequest("user@example.com")))
                .andExpect(status().isAccepted());

        verify(passwordRecoveryService).requestRecovery("user@example.com");
    }

    @Test
    void recoveryNormalizesEmailBeforeValidationAndPassesItDownstream() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recoveryRequest("  USER@Example.COM  ")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value(NEUTRAL_MESSAGE));

        verify(passwordRecoveryService).requestRecovery("user@example.com");
    }

    @Test
    void recoveryStillRejectsInvalidEmailSyntax() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password-recovery")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(recoveryRequest("not-an-email")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());

        verify(passwordRecoveryService, never())
                .requestRecovery(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void resetRequiresCsrf() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetRequest("token", "new-password", "new-password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void validResetReturnsNoContentWithoutAuthenticatingUser() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password-reset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetRequest("token", "new-password", "new-password")))
                .andExpect(status().isNoContent());
    }

    @Test
    void unknownExpiredAndUsedTokensHaveSameExternalError() throws Exception {
        for (String token : new String[]{"unknown-token", "expired-token", "used-token"}) {
            doThrow(new InvalidPasswordResetTokenException())
                    .when(passwordResetService)
                    .resetPassword(argThat(request -> token.equals(request.token())));

            mockMvc.perform(post("/api/v1/auth/password-reset")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(resetRequest(token, "new-password", "new-password")))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Token de recuperação inválido"))
                    .andExpect(jsonPath("$.detail")
                            .value("Token de recuperação inválido ou expirado."));
        }
    }

    @Test
    void resetRejectsPasswordConfirmationMismatch() throws Exception {
        mockMvc.perform(post("/api/v1/auth/password-reset")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resetRequest("token", "password-one", "password-two")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.errors.passwordConfirmationMatching").exists());

        verify(passwordResetService, never())
                .resetPassword(org.mockito.ArgumentMatchers.any());
    }

    private String recoveryRequest(String email) {
        return """
                {
                  "email": "%s"
                }
                """.formatted(email);
    }

    private String resetRequest(String token, String password, String confirmation) {
        return """
                {
                  "token": "%s",
                  "password": "%s",
                  "passwordConfirmation": "%s"
                }
                """.formatted(token, password, confirmation);
    }
}
