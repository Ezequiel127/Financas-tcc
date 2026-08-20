package com.financastcc.backend.identity.presentation;

import com.financastcc.backend.identity.application.UserRegistrationService;
import com.financastcc.backend.identity.application.dto.UserRegistrationResponse;
import com.financastcc.backend.identity.application.exception.EmailAlreadyExistsException;
import com.financastcc.backend.identity.infrastructure.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfiguration.class, RegistrationExceptionHandler.class})
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRegistrationService userRegistrationService;

    @Test
    void rejectsRegistrationWithoutCsrfToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnsCreatedWithoutExposingPasswordData() throws Exception {
        when(userRegistrationService.register(any())).thenReturn(
                new UserRegistrationResponse(
                        1L,
                        "user@example.com",
                        Instant.parse("2026-08-20T12:00:00Z")
                )
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.createdAt").value("2026-08-20T12:00:00Z"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void returnsBadRequestWhenPasswordsDoNotMatchWithoutExposingValues() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "password": "password-one",
                                  "passwordConfirmation": "password-two"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.errors.passwordConfirmationMatching").exists())
                .andExpect(jsonPath("$.errors.password").doesNotExist())
                .andExpect(jsonPath("$.errors.passwordConfirmation").doesNotExist());
    }

    @Test
    void returnsConflictWhenEmailAlreadyExists() throws Exception {
        when(userRegistrationService.register(any())).thenThrow(new EmailAlreadyExistsException());

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito de e-mail"))
                .andExpect(jsonPath("$.detail").value("E-mail já cadastrado."));
    }

    private String validRequest() {
        return """
                {
                  "email": "user@example.com",
                  "password": "plain-password",
                  "passwordConfirmation": "plain-password"
                }
                """;
    }
}
