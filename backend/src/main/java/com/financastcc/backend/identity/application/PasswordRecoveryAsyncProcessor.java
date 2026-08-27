package com.financastcc.backend.identity.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PasswordRecoveryAsyncProcessor {

    public static final String EXECUTOR_NAME = "passwordRecoveryTaskExecutor";

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PasswordRecoveryAsyncProcessor.class);

    private final PasswordRecoveryWorker worker;

    public PasswordRecoveryAsyncProcessor(PasswordRecoveryWorker worker) {
        this.worker = worker;
    }

    @Async(EXECUTOR_NAME)
    public void processRecovery(String normalizedEmail) {
        try {
            worker.processRecovery(normalizedEmail);
        } catch (RuntimeException exception) {
            LOGGER.error("Falha operacional no processamento da recuperação de senha.");
        }
    }
}
