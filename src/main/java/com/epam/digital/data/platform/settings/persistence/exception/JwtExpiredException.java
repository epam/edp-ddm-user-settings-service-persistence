package com.epam.digital.data.platform.settings.persistence.exception;

import com.epam.digital.data.platform.model.core.kafka.Status;

public class JwtExpiredException extends RequestProcessingException {
    public JwtExpiredException(String message) {
        super(message, Status.JWT_EXPIRED);
    }
}
