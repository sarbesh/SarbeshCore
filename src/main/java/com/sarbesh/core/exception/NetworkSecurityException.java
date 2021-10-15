package com.sarbesh.core.exception;

import com.sarbesh.core.dto.AccessErrorType;

public class NetworkSecurityException extends Exception{
    private static final long serialVersionUID = 1L;
    private AccessErrorType error;

    public NetworkSecurityException(AccessErrorType error) {
        this.error = error;
    }

    public AccessErrorType getError() {
        return error;
    }
}
