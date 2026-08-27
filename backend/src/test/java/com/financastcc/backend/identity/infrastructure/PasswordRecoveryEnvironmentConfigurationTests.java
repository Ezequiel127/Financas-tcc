package com.financastcc.backend.identity.infrastructure;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordRecoveryEnvironmentConfigurationTests {

    @Test
    void commonConfigurationRequiresProductionEnvironmentValues() throws IOException {
        Properties properties = load("application.properties");

        assertThat(properties.getProperty("spring.mail.host")).isEqualTo("${MAIL_HOST}");
        assertThat(properties.getProperty("spring.mail.port")).isEqualTo("${MAIL_PORT}");
        assertThat(properties.getProperty("spring.mail.username"))
                .isEqualTo("${MAIL_USERNAME}");
        assertThat(properties.getProperty("spring.mail.password"))
                .isEqualTo("${MAIL_PASSWORD}");
        assertThat(properties.getProperty("app.password-reset.mail-from"))
                .isEqualTo("${MAIL_FROM}");
        assertThat(properties.getProperty("app.password-reset.frontend-base-url"))
                .isEqualTo("${FRONTEND_BASE_URL}");
    }

    @Test
    void developmentConfigurationProvidesLocalOverridableDefaults() throws IOException {
        Properties properties = load("application-dev.properties");

        assertThat(properties.getProperty("spring.mail.host"))
                .isEqualTo("${MAIL_HOST:localhost}");
        assertThat(properties.getProperty("spring.mail.port"))
                .isEqualTo("${MAIL_PORT:1025}");
        assertThat(properties.getProperty("spring.mail.username"))
                .isEqualTo("${MAIL_USERNAME:}");
        assertThat(properties.getProperty("spring.mail.password"))
                .isEqualTo("${MAIL_PASSWORD:}");
        assertThat(properties.getProperty("app.password-reset.mail-from"))
                .isEqualTo("${MAIL_FROM:no-reply@localhost}");
        assertThat(properties.getProperty("app.password-reset.frontend-base-url"))
                .isEqualTo("${FRONTEND_BASE_URL:http://localhost:3000}");
    }

    @Test
    void testConfigurationUsesSafeValuesWithoutRealCredentials() throws IOException {
        Properties properties = load("application-test.properties");

        assertThat(properties.getProperty("spring.mail.host"))
                .isEqualTo("smtp.test.invalid");
        assertThat(properties.getProperty("spring.mail.port")).isEqualTo("2525");
        assertThat(properties.getProperty("spring.mail.username")).isEmpty();
        assertThat(properties.getProperty("spring.mail.password")).isEmpty();
        assertThat(properties.getProperty("app.password-reset.mail-from"))
                .isEqualTo("no-reply@test.invalid");
        assertThat(properties.getProperty("app.password-reset.frontend-base-url"))
                .isEqualTo("http://frontend.test");
    }

    private Properties load(String resourceName) throws IOException {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream(resourceName)) {
            assertThat(input).as(resourceName).isNotNull();
            properties.load(input);
        }
        return properties;
    }
}
