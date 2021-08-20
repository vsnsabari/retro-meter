package com.vsnsabari.retrometer.exceptions;

public class InvalidCommentTypeException extends RuntimeException {

    public InvalidCommentTypeException(String message) {
        super(message);
    }

    public InvalidCommentTypeException(Throwable throwable) {
        super(throwable);
    }
}
