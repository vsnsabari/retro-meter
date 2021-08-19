package com.vsnsabari.retrometer.exceptions;

public class CommentCreationException extends RuntimeException {

    public CommentCreationException(String message) {
        super(message);
    }

    public CommentCreationException(Throwable throwable) {
        super(throwable);
    }
}
