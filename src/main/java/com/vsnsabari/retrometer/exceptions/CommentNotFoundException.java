package com.vsnsabari.retrometer.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(long commentId) {
        super(String.format("No Comment found with id : %d", commentId));
    }
}
