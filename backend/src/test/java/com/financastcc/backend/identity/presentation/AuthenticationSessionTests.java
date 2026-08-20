package com.financastcc.backend.identity.presentation;

import com.financastcc.backend.identity.application.UserAuthenticationService;
import com.financastcc.backend.identity.application.UserRegistrationService;
import com.financastcc.backend.identity.application.security.AuthenticatedUserPrincipal;
import com.financastcc.backend.identity.domain.User;
import com.financastcc.backend.identity.infrastructure.DatabaseUserDetailsService;
import com.financastcc.backend.identity.infrastructure.SecurityConfiguration;
import com.financastcc.backend.identity.infrastructure.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfiguration.class,
        RegistrationExceptionHandler.class,
        UserAuthenticationService.class,
        DatabaseUserDetailsService.class
})
class AuthenticationSessionTests {

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD = "correct-password";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserRegistrationService userRegistrationService;

    @Test
    void logsInWithValidCredentialsAndReturnsOnlySafeUserData() throws Exception {
        configureExistingUser(PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest("  USER@Example.COM  ", PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.credentials").doesNotExist());
    }

    @Test
    void returnsGenericUnauthorizedForUnknownEmail() throws Exception {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertGenericUnauthorized(
                mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest(EMAIL, PASSWORD)))
                        .andReturn()
        );
    }

    @Test
    void returnsGenericUnauthorizedForWrongPassword() throws Exception {
        configureExistingUser(PASSWORD);

        assertGenericUnauthorized(
                mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest(EMAIL, "wrong-password")))
                        .andReturn()
        );
    }

    @Test
    void persistsAuthenticationInSessionAndMeReturnsCurrentUser() throws Exception {
        configureExistingUser(PASSWORD);

        MvcResult loginResult = loginWithCsrf();
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        assertThat(session).isNotNull();
        SecurityContext context = (SecurityContext) session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        );
        assertThat(context).isNotNull();
        assertThat(context.getAuthentication().isAuthenticated()).isTrue();
        AuthenticatedUserPrincipal principal =
                (AuthenticatedUserPrincipal) context.getAuthentication().getPrincipal();
        assertThat(principal.id()).isEqualTo(42L);
        assertThat(principal.getPassword()).isNull();

        mockMvc.perform(get("/api/v1/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void meReturnsUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginRotatesSessionAndReplacesPreAuthenticationCsrfToken() throws Exception {
        configureExistingUser(PASSWORD);

        MvcResult csrfResult = mockMvc.perform(get("/api/v1/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerName").isNotEmpty())
                .andExpect(jsonPath("$.parameterName").isNotEmpty())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String csrfBody = csrfResult.getResponse().getContentAsString();
        String headerName = JsonPath.read(csrfBody, "$.headerName");
        String token = JsonPath.read(csrfBody, "$.token");
        MockHttpSession session = (MockHttpSession) csrfResult.getRequest().getSession(false);
        assertThat(session).isNotNull();
        String anonymousSessionId = session.getId();

        mockMvc.perform(post("/api/v1/auth/login")
                        .session(session)
                        .header(headerName, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest(EMAIL, PASSWORD)))
                .andExpect(status().isOk());

        assertThat(session.getId()).isNotEqualTo(anonymousSessionId);

        mockMvc.perform(post("/api/v1/auth/logout")
                        .session(session)
                        .header(headerName, token))
                .andExpect(status().isForbidden());
        assertThat(session.isInvalid()).isFalse();

        MvcResult freshCsrfResult = mockMvc.perform(get("/api/v1/auth/csrf").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headerName").isNotEmpty())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
        String freshCsrfBody = freshCsrfResult.getResponse().getContentAsString();
        String freshHeaderName = JsonPath.read(freshCsrfBody, "$.headerName");
        String freshToken = JsonPath.read(freshCsrfBody, "$.token");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .session(session)
                        .header(freshHeaderName, freshToken))
                .andExpect(status().isNoContent());
        assertThat(session.isInvalid()).isTrue();
    }

    @Test
    void loginRequiresCsrfToken() throws Exception {
        configureExistingUser(PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest(EMAIL, PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginRejectsBlankRequiredFields() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest(" ", " ")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void logoutRequiresCsrfToken() throws Exception {
        configureExistingUser(PASSWORD);
        MockHttpSession session = authenticatedSession();

        mockMvc.perform(post("/api/v1/auth/logout").session(session))
                .andExpect(status().isForbidden());

        assertThat(session.isInvalid()).isFalse();
    }

    @Test
    void logoutReturnsUnauthorizedWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutInvalidatesSessionAndClearsAuthentication() throws Exception {
        configureExistingUser(PASSWORD);
        MockHttpSession session = authenticatedSession();

        mockMvc.perform(post("/api/v1/auth/logout")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertThat(session.isInvalid()).isTrue();
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    private MvcResult loginWithCsrf() throws Exception {
        return mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MockHttpSession authenticatedSession() throws Exception {
        return (MockHttpSession) loginWithCsrf().getRequest().getSession(false);
    }

    private void configureExistingUser(String rawPassword) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(42L);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getPasswordHash()).thenReturn(passwordEncoder.encode(rawPassword));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
    }

    private void assertGenericUnauthorized(MvcResult result) throws Exception {
        assertThat(result.getResponse().getStatus()).isEqualTo(401);
        assertThat(JsonPath.<String>read(
                result.getResponse().getContentAsString(),
                "$.detail"
        )).isEqualTo("Credenciais inválidas.");
    }

    private String loginRequest(String email, String password) {
        return """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);
    }
}
