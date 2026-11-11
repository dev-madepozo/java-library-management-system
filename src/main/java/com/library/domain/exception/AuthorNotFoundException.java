package com.library.domain.exception;

public class AuthorNotFoundException extends RuntimeException {

    public AuthorNotFoundException(String message) {
        super(message);
    }
}
