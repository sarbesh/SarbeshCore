package com.sarbesh.core.exception;

public class BadRequestException extends Exception{
    private static final long serialVersionUID = 1L;

    public BadRequestException() {
    }

    public BadRequestException(String s) {
        super(s);
    }

    public BadRequestException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
