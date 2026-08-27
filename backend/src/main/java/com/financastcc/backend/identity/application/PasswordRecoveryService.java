package com.financastcc.backend.identity.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

@Service
public class PasswordRecoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordRecoveryService.class);
    private final PasswordRecoveryAsyncProcessor asyncProcessor;

    public PasswordRecoveryService(PasswordRecoveryAsyncProcessor asyncProcessor) {
        this.asyncProcessor = asyncProcessor;
    }

    public void requestRecovery(String email) {
        try {
            asyncProcessor.processRecovery(email);
        } catch (TaskRejectedException exception) {
            LOGGER.error("Fila de recuperação de senha temporariamente indisponível.");
        }
    }
}
