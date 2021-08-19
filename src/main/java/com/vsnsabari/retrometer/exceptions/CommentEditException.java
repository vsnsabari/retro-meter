package com.vsnsabari.retrometer.exceptions;

public class CommentEditException extends RuntimeException {

    public CommentEditException(String message) {
        super(message);
    }

    public CommentEditException(Throwable throwable) {
        super(throwable);
    }
}
