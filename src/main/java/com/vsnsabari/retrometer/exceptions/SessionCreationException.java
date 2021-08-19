package com.vsnsabari.retrometer.exceptions;

public class SessionCreationException extends RuntimeException {
    public SessionCreationException(String message) {
        super(message);
    }

    public SessionCreationException(Throwable throwable) {
        super(throwable);
    }
}
