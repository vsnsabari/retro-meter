package com.vsnsabari.retrometer.exceptions;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(String sessionId) {
        super(String.format("Session not found with id %s", sessionId));
    }
}
