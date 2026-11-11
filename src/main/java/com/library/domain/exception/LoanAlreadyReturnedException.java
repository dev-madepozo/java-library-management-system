package com.library.domain.exception;

public class LoanAlreadyReturnedException extends RuntimeException {
    public LoanAlreadyReturnedException(String message) {
        super(message);
    }
}
