package com.library.domain.exception;

public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String message) {
        super(message);
    }
}
