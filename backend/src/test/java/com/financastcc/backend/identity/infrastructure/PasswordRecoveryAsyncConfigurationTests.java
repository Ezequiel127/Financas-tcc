package com.financastcc.backend.identity.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordRecoveryAsyncConfigurationTests {

    @Test
    void createsDedicatedBoundedExecutor() {
        ThreadPoolTaskExecutor executor = new PasswordRecoveryAsyncConfiguration()
                .passwordRecoveryTaskExecutor();

        assertThat(executor.getCorePoolSize()).isEqualTo(2);
        assertThat(executor.getMaxPoolSize()).isEqualTo(4);
        assertThat(executor.getQueueCapacity()).isEqualTo(100);
        assertThat(executor.getThreadNamePrefix()).isEqualTo("password-recovery-");
    }
}
